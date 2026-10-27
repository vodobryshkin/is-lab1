package com.github.vodobryshkin.islab1.backend.events;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.CloseReason;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.server.ServerEndpointConfig;
import java.nio.ByteBuffer;

/**
 * WebSocket-канал уведомлений /api/v1/events для вошедших пользователей.
 */
@ServerEndpoint(value = "/api/v1/events", configurator = EventsEndpoint.Handshake.class)
public class EventsEndpoint {
    private static final String HTTP_SESSION = "httpSession";
    private static final short CLIENT_MESSAGE = 4002;
    private static final String INVALID_MESSAGE = "{\"type\":\"ERROR\",\"code\":"
            + "\"INVALID_MESSAGE\",\"description\":\"Client messages are not supported.\"}";

    private final EventSessions sessions;

    /**
     * Создаёт endpoint для одного соединения.
     *
     * @param sessions реестр соединений
     */
    @Inject
    public EventsEndpoint(EventSessions sessions) {
        this.sessions = sessions;
    }

    /** Нужен контейнеру WebSocket; прикладной код его не вызывает. */
    public EventsEndpoint() {
        this(null);
    }

    /**
     * Регистрирует соединение и подтверждает вход сообщением AUTHENTICATED.
     *
     * @param connection открытое соединение
     */
    @OnOpen
    public void open(Session connection) {
        sessions.add(connection, (HttpSession) connection.getUserProperties().get(HTTP_SESSION));
        sessions.send(connection, "{\"type\":\"AUTHENTICATED\"}");
    }

    /**
     * Отклоняет текстовое сообщение клиента: INVALID_MESSAGE и код 4002.
     *
     * @param connection соединение
     * @param message сообщение клиента
     */
    @OnMessage
    public void text(Session connection, String message) {
        sessions.remove(connection);
        sessions.close(connection, CLIENT_MESSAGE, INVALID_MESSAGE, "Client message");
    }

    /**
     * Отклоняет бинарное сообщение кодом 1003.
     *
     * @param connection соединение
     * @param message сообщение клиента
     */
    @OnMessage
    public void binary(Session connection, ByteBuffer message) {
        sessions.remove(connection);
        sessions.close(connection, (short) CloseReason.CloseCodes.CANNOT_ACCEPT.getCode(), null,
                "Binary message");
    }

    /**
     * Убирает закрытое соединение из реестра.
     *
     * @param connection соединение
     */
    @OnClose
    public void close(Session connection) {
        sessions.remove(connection);
    }

    /**
     * Убирает соединение, отправка в которое не удалась.
     *
     * @param connection соединение
     * @param error ошибка соединения
     */
    @OnError
    public void error(Session connection, Throwable error) {
        sessions.remove(connection);
    }

    /**
     * Передаёт соединению HTTP-сессию запроса Upgrade.
     */
    public static class Handshake extends ServerEndpointConfig.Configurator {
        @Override
        public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request,
                HandshakeResponse response) {
            config.getUserProperties().put(HTTP_SESSION, request.getHttpSession());
        }
    }
}
