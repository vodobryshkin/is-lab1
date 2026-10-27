package com.github.vodobryshkin.islab1.backend.service;

import com.github.vodobryshkin.islab1.backend.domain.Location;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import com.github.vodobryshkin.islab1.backend.events.EventSessions;
import com.github.vodobryshkin.islab1.backend.repository.LocationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Операции с локациями.
 */
@ApplicationScoped
public class LocationService {
    private final LocationRepository repository;
    private final EventSessions events;

    /**
     * Создаёт сервис.
     *
     * @param repository хранилище локаций
     * @param events уведомление об изменениях после фиксации транзакции
     */
    @Inject
    public LocationService(LocationRepository repository, EventSessions events) {
        this.repository = repository;
        this.events = events;
    }

    /**
     * Нужен CDI для создания клиентского прокси; прикладной код его не вызывает.
     */
    protected LocationService() {
        this.repository = null;
        this.events = null;
    }

    /**
     * Сохраняет новую локацию.
     *
     * @param location проверенная локация без ID
     * @return ID новой локации
     */
    @Transactional
    public Long create(Location location) {
        repository.save(location);
        events.changed();
        return location.getId();
    }

    /**
     * Сохраняет новую локацию (без ID) или находит существующую по ID в транзакции
     * вызывающего сервиса.
     *
     * @param location новая локация или ссылка, у которой задан только ID
     * @return сохранённая локация или пустой результат, если локации с таким ID нет
     */
    public Optional<Location> persistOrFind(Location location) {
        if (location.getId() != null) {
            return repository.findById(location.getId());
        }
        repository.save(location);
        return Optional.of(location);
    }

    /**
     * Ищет локацию.
     *
     * @param id ID локации
     * @return локация или пустой результат
     */
    public Optional<Location> find(long id) {
        return repository.findById(id);
    }

    /**
     * Возвращает страницу локаций.
     *
     * @param query параметры страницы
     * @return локации страницы
     */
    public List<Location> findPage(ListQuery query) {
        return repository.findPage(query);
    }

    /**
     * Считает все локации.
     *
     * @return количество
     */
    public long count() {
        return repository.count();
    }

    /**
     * Заменяет все поля локации, в том числе используемой адресами и персонами.
     *
     * @param id ID локации
     * @param values новые значения
     * @return false, если локации нет
     */
    @Transactional
    public boolean update(long id, Location values) {
        Optional<Location> location = repository.findById(id);
        location.ifPresent(found -> {
            found.setX(values.getX());
            found.setY(values.getY());
            found.setZ(values.getZ());
            events.changed();
        });
        return location.isPresent();
    }

    /**
     * Удаляет локацию, если на неё не ссылаются адреса и персоны.
     * Связанные объекты при этом не отвязывает и не удаляет.
     *
     * @param id ID локации
     * @return результат удаления
     */
    @Transactional
    public Outcome delete(long id) {
        Optional<Location> location = repository.findById(id);
        if (location.isEmpty()) {
            return Outcome.NOT_FOUND;
        }
        if (repository.isUsed(id)) {
            return Outcome.IN_USE;
        }
        repository.delete(location.get());
        events.changed();
        return Outcome.DONE;
    }
}
