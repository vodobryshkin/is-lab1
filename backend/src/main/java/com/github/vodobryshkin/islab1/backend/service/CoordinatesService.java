package com.github.vodobryshkin.islab1.backend.service;

import com.github.vodobryshkin.islab1.backend.domain.Coordinates;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import com.github.vodobryshkin.islab1.backend.events.EventSessions;
import com.github.vodobryshkin.islab1.backend.repository.CoordinatesRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Операции с координатами.
 */
@ApplicationScoped
public class CoordinatesService {
    private final CoordinatesRepository repository;
    private final EventSessions events;

    /**
     * Создаёт сервис.
     *
     * @param repository хранилище координат
     * @param events уведомление об изменениях после фиксации транзакции
     */
    @Inject
    public CoordinatesService(CoordinatesRepository repository, EventSessions events) {
        this.repository = repository;
        this.events = events;
    }

    /** Нужен CDI для создания клиентского прокси; прикладной код его не вызывает. */
    protected CoordinatesService() {
        this.repository = null;
        this.events = null;
    }

    /**
     * Сохраняет новые координаты.
     *
     * @param coordinates проверенные координаты без ID
     * @return ID новых координат
     */
    @Transactional
    public Long create(Coordinates coordinates) {
        repository.save(coordinates);
        events.changed();
        return coordinates.getId();
    }

    /**
     * Ищет координаты.
     *
     * @param id ID координат
     * @return координаты или пустой результат
     */
    public Optional<Coordinates> find(long id) {
        return repository.findById(id);
    }

    /**
     * Возвращает страницу координат.
     *
     * @param query параметры страницы
     * @return координаты страницы
     */
    public List<Coordinates> findPage(ListQuery query) {
        return repository.findPage(query);
    }

    /**
     * Считает все координаты.
     *
     * @return количество
     */
    public long count() {
        return repository.count();
    }

    /**
     * Заменяет все поля координат, в том числе используемых работниками.
     *
     * @param id ID координат
     * @param values новые значения
     * @return {@code false}, если координат нет
     */
    @Transactional
    public boolean update(long id, Coordinates values) {
        Optional<Coordinates> coordinates = repository.findById(id);
        coordinates.ifPresent(found -> {
            found.setX(values.getX());
            found.setY(values.getY());
            events.changed();
        });
        return coordinates.isPresent();
    }

    /**
     * Удаляет координаты, если на них не ссылаются работники.
     * Работников при этом не отвязывает и не удаляет.
     *
     * @param id ID координат
     * @return результат удаления
     */
    @Transactional
    public Outcome delete(long id) {
        Optional<Coordinates> coordinates = repository.findById(id);
        if (coordinates.isEmpty()) {
            return Outcome.NOT_FOUND;
        }
        if (repository.isUsed(id)) {
            return Outcome.IN_USE;
        }
        repository.delete(coordinates.get());
        events.changed();
        return Outcome.DONE;
    }
}
