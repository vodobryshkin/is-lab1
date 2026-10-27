package com.github.vodobryshkin.islab1.backend.service;

import com.github.vodobryshkin.islab1.backend.domain.Address;
import com.github.vodobryshkin.islab1.backend.domain.Location;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import com.github.vodobryshkin.islab1.backend.events.EventSessions;
import com.github.vodobryshkin.islab1.backend.repository.AddressRepository;
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
    private final AddressRepository repository;
    private final LocationService locationService;
    private final EventSessions events;

    /**
     * Создаёт сервис.
     *
     * @param repository хранилище адресов
     * @param locationService локации
     * @param events уведомление об изменениях после фиксации транзакции
     */
    @Inject
    public AddressService(AddressRepository repository, LocationService locationService,
            EventSessions events) {
        this.repository = repository;
        this.locationService = locationService;
        this.events = events;
    }

    /**
     * Нужен CDI для создания клиентского прокси; прикладной код его не вызывает.
     */
    protected AddressService() {
        this.repository = null;
        this.locationService = null;
        this.events = null;
    }

    /**
     * Сохраняет адрес и новую локацию в одной транзакции.
     *
     * @param address адрес; у town задан только ID, если это существующая локация
     * @return ID адреса или пустой результат, если локации с таким ID нет
     */
    @Transactional
    public Optional<Long> create(Address address) {
        if (!attachTown(address, address.getTown())) {
            return Optional.empty();
        }
        repository.save(address);
        events.changed();
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
     * @param street подстрока улицы или null
     * @return адреса страницы
     */
    public List<Address> findPage(ListQuery query, String street) {
        return repository.findPage(query, street);
    }

    /**
     * Считает адреса с учётом фильтра.
     *
     * @param street подстрока улицы или null
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
     * @return DONE, NOT_FOUND или REFERENCE_NOT_FOUND
     */
    @Transactional
    public Outcome update(long id, Address values) {
        Optional<Address> address = repository.findById(id);
        if (address.isEmpty()) {
            return Outcome.NOT_FOUND;
        }
        if (!attachTown(address.get(), values.getTown())) {
            return Outcome.REFERENCE_NOT_FOUND;
        }
        address.get().setStreet(values.getStreet());
        events.changed();
        return Outcome.DONE;
    }

    /**
     * Удаляет адрес, если его не используют организации. Локацию адреса не удаляет.
     *
     * @param id ID адреса
     * @return DONE, NOT_FOUND или IN_USE
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
        events.changed();
        return Outcome.DONE;
    }

    /**
     * Привязывает город: null, новую локацию или существующую по ID.
     *
     * @return false, если локации с указанным ID нет; адрес тогда не меняется
     */
    private boolean attachTown(Address address, Location town) {
        if (town == null) {
            address.setTown(null);
            return true;
        }
        Optional<Location> found = locationService.persistOrFind(town);
        found.ifPresent(address::setTown);
        return found.isPresent();
    }
}
