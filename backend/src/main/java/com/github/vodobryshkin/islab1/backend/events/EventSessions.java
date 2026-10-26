package com.github.vodobryshkin.islab1.backend.events;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;
import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Открытые WebSocket-соединения вошедших пользователей и HTTP-сессии, в которых они
 * открыты, и уведомление {@code DATA_CHANGED} после фиксации изменяющей транзакции.
 * Отправки в одно соединение идут по очереди под его блокировкой: стандартный
 * {@code RemoteEndpoint.Basic} не допускает одновременных отправок.
 */
@ApplicationScoped
public class EventSessions {
    /** Код закрытия контракта: вход сессии завершён. */
    static final short SESSION_ENDED = 4001;

    private static final String SESSION_ENDED_MESSAGE = "{\"type\":\"ERROR\",\"code\":"
            + "\"SESSION_ENDED\",\"description\":\"The session has ended. Log in again and "
            + "reconnect.\"}";

    private static final String LOCK = "lock";
    private static final String DATA_CHANGED = "{\"type\":\"DATA_CHANGED\"}";

    /** Ключ отметки «уведомление уже запланировано» в текущей JTA-транзакции. */
    private static final Object PENDING = new Object();

    private final Map<Session, HttpSession> connections = new ConcurrentHashMap<>();

    @Resource
    private TransactionSynchronizationRegistry transactions;

    /**
     * Отмечает, что текущая транзакция изменила данные. После её фиксации все
     * соединения получат одно {@code DATA_CHANGED}, сколько бы раз метод ни вызвали
     * в этой транзакции; при откате уведомления нет. Вызывается сервисом, когда
     * бизнес-сценарий действительно изменил данные.
     */
    public void changed() {
        if (transactions.getResource(PENDING) != null) {
            return;
        }
        transactions.putResource(PENDING, Boolean.TRUE);
        transactions.registerInterposedSynchronization(new Synchronization() {
            @Override
            public void beforeCompletion() {
                // Уведомление нужно только после результата транзакции.
            }

            @Override
            public void afterCompletion(int status) {
                if (status == Status.STATUS_COMMITTED) {
                    connections.keySet().forEach(connection -> send(connection, DATA_CHANGED));
                }
            }
        });
    }

    /**
     * Регистрирует открытое соединение.
     *
     * @param connection WebSocket-соединение
     * @param httpSession HTTP-сессия, в которой выполнен вход
     */
    public void add(Session connection, HttpSession httpSession) {
        connection.getUserProperties().put(LOCK, new ReentrantLock());
        connections.put(connection, httpSession);
    }

    /**
     * Убирает закрытое соединение.
     *
     * @param connection WebSocket-соединение
     */
    public void remove(Session connection) {
        connections.remove(connection);
    }

    /**
     * Завершает соединения HTTP-сессии при выходе или повторном входе: отправляет
     * {@code SESSION_ENDED} и закрывает их кодом 4001. При истечении тайм-аута соединения
     * закрывает сам контейнер кодом 1008 (Jakarta WebSocket 2.1, WSC-7.2-3).
     *
     * @param httpSession HTTP-сессия
     */
    public void end(HttpSession httpSession) {
        connections.forEach((connection, owner) -> {
            if (owner.getId().equals(httpSession.getId())) {
                connections.remove(connection);
                close(connection, SESSION_ENDED, SESSION_ENDED_MESSAGE, "Session ended");
            }
        });
    }

    /**
     * Отправляет сообщение одному соединению. Ошибка отправки только закрывает это
     * соединение и не влияет на остальные.
     *
     * @param connection WebSocket-соединение
     * @param message текстовый JSON
     */
    void send(Session connection, String message) {
        Lock lock = lockOf(connection);
        lock.lock();
        try {
            connection.getBasicRemote().sendText(message);
        } catch (IOException | IllegalStateException exception) {
            remove(connection);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Отправляет сообщение, если оно задано, и закрывает соединение.
     *
     * @param connection WebSocket-соединение
     * @param code код закрытия
     * @param message текстовый JSON перед закрытием или {@code null}
     * @param reason причина закрытия
     */
    void close(Session connection, short code, String message, String reason) {
        if (message != null) {
            send(connection, message);
        }
        Lock lock = lockOf(connection);
        lock.lock();
        try {
            connection.close(new CloseReason(CloseReason.CloseCodes.getCloseCode(code), reason));
        } catch (IOException | IllegalStateException exception) {
            remove(connection);
        } finally {
            lock.unlock();
        }
    }

    private Lock lockOf(Session connection) {
        return (Lock) connection.getUserProperties().get(LOCK);
    }
}
