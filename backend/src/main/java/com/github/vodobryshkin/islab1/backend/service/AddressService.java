package com.github.vodobryshkin.islab1.backend.service;

import com.github.vodobryshkin.islab1.backend.domain.Address;
import com.github.vodobryshkin.islab1.backend.domain.Location;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import com.github.vodobryshkin.islab1.backend.repository.AddressRepository;
import com.github.vodobryshkin.islab1.backend.repository.LocationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Операции с адресами. Город адреса — новая локация, существующая локация или её отсутствие.
 */
@ApplicationScoped
public class AddressService {
    /** Результат изменения или удаления. */
    public enum Outcome {
        DONE,
        NOT_FOUND,
        TOWN_NOT_FOUND,
        IN_USE
    }

    private final AddressRepository repository;
    private final LocationRepository locationRepository;

    /**
     * Создаёт сервис.
     *
     * @param repository хранилище адресов
     * @param locationRepository хранилище локаций
     */
    @Inject
    public AddressService(AddressRepository repository, LocationRepository locationRepository) {
        this.repository = repository;
        this.locationRepository = locationRepository;
    }

    /** Нужен CDI для создания клиентского прокси; прикладной код его не вызывает. */
    protected AddressService() {
        this.repository = null;
        this.locationRepository = null;
    }

    /**
     * Сохраняет адрес и новую локацию в одной транзакции.
     *
     * @param address адрес; у {@code town} задан только ID, если это существующая локация
     * @return ID адреса или пустой результат, если локации с таким ID нет
     */
    @Transactional
    public Optional<Long> create(Address address) {
        if (!townExists(address.getTown())) {
            return Optional.empty();
        }
        address.setTown(persistentTown(address.getTown()));
        repository.save(address);
        return Optional.of(address.getId());
    }

    /**
     * Ищет адрес.
     *
     * @param id ID адреса
     * @return адрес или пустой результат
     */
    public Optional<Address> find(long id) {
        return repository.findById(id);
    }

    /**
     * Возвращает страницу адресов.
     *
     * @param query параметры страницы
     * @param street подстрока улицы или {@code null}
     * @return адреса страницы
     */
    public List<Address> findPage(ListQuery query, String street) {
        return repository.findPage(query, street);
    }

    /**
     * Считает адреса с учётом фильтра.
     *
     * @param street подстрока улицы или {@code null}
     * @return количество
     */
    public long count(String street) {
        return repository.count(street);
    }

    /**
     * Заменяет улицу и город. Прежняя локация при этом не удаляется.
     *
     * @param id ID адреса
     * @param values новые значения
     * @return {@code DONE}, {@code NOT_FOUND} или {@code TOWN_NOT_FOUND}
     */
    @Transactional
    public Outcome update(long id, Address values) {
        Optional<Address> address = repository.findById(id);
        if (address.isEmpty()) {
            return Outcome.NOT_FOUND;
        }
        if (!townExists(values.getTown())) {
            return Outcome.TOWN_NOT_FOUND;
        }
        address.get().setStreet(values.getStreet());
        address.get().setTown(persistentTown(values.getTown()));
        return Outcome.DONE;
    }

    /**
     * Удаляет адрес, если его не используют организации. Локацию адреса не удаляет.
     *
     * @param id ID адреса
     * @return {@code DONE}, {@code NOT_FOUND} или {@code IN_USE}
     */
    @Transactional
    public Outcome delete(long id) {
        Optional<Address> address = repository.findById(id);
        if (address.isEmpty()) {
            return Outcome.NOT_FOUND;
        }
        if (repository.isUsed(id)) {
            return Outcome.IN_USE;
        }
        repository.delete(address.get());
        return Outcome.DONE;
    }

    /** Город без ID — новая локация, с ID — ссылка на существующую. */
    private boolean townExists(Location town) {
        return town == null || town.getId() == null
                || locationRepository.findById(town.getId()).isPresent();
    }

    private Location persistentTown(Location town) {
        if (town == null) {
            return null;
        }
        if (town.getId() != null) {
            return locationRepository.findById(town.getId()).orElseThrow();
        }
        locationRepository.save(town);
        return town;
    }
}
