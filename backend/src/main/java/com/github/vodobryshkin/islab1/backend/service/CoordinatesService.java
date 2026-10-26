package com.github.vodobryshkin.islab1.backend.service;

import com.github.vodobryshkin.islab1.backend.domain.Coordinates;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
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
    /** Результат удаления. */
    public enum Deletion {
        DELETED,
        NOT_FOUND,
        IN_USE
    }

    private final CoordinatesRepository repository;

    /**
     * Создаёт сервис.
     *
     * @param repository хранилище координат
     */
    @Inject
    public CoordinatesService(CoordinatesRepository repository) {
        this.repository = repository;
    }

    /** Нужен CDI для создания клиентского прокси; прикладной код его не вызывает. */
    protected CoordinatesService() {
        this.repository = null;
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
    public Deletion delete(long id) {
        Optional<Coordinates> coordinates = repository.findById(id);
        if (coordinates.isEmpty()) {
            return Deletion.NOT_FOUND;
        }
        if (repository.isUsed(id)) {
            return Deletion.IN_USE;
        }
        repository.delete(coordinates.get());
        return Deletion.DELETED;
    }
}
