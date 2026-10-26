package com.github.vodobryshkin.islab1.backend.service;

import com.github.vodobryshkin.islab1.backend.domain.Address;
import com.github.vodobryshkin.islab1.backend.domain.Location;
import com.github.vodobryshkin.islab1.backend.domain.Organization;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import com.github.vodobryshkin.islab1.backend.events.EventSessions;
import com.github.vodobryshkin.islab1.backend.repository.AddressRepository;
import com.github.vodobryshkin.islab1.backend.repository.LocationRepository;
import com.github.vodobryshkin.islab1.backend.repository.OrganizationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Операции с организациями. Каждый адрес — новый (с новой, существующей или без
 * локации) либо существующий по ID. Все ссылки проверяются до сохранения, поэтому
 * при отказе не остаётся частично созданных адресов и локаций.
 */
@ApplicationScoped
public class OrganizationService {
    private final OrganizationRepository repository;
    private final AddressRepository addressRepository;
    private final LocationRepository locationRepository;
    private final EventSessions events;

    /**
     * Создаёт сервис.
     *
     * @param repository хранилище организаций
     * @param addressRepository хранилище адресов
     * @param locationRepository хранилище локаций
     * @param events уведомление об изменениях после фиксации транзакции
     */
    @Inject
    public OrganizationService(OrganizationRepository repository,
            AddressRepository addressRepository, LocationRepository locationRepository,
            EventSessions events) {
        this.repository = repository;
        this.addressRepository = addressRepository;
        this.locationRepository = locationRepository;
        this.events = events;
    }

    /** Нужен CDI для создания клиентского прокси; прикладной код его не вызывает. */
    protected OrganizationService() {
        this.repository = null;
        this.addressRepository = null;
        this.locationRepository = null;
        this.events = null;
    }

    /**
     * Сохраняет организацию вместе с новыми адресами и локациями в одной транзакции.
     *
     * @param organization организация; у ссылки на адрес или локацию задан только ID
     * @return {@code DONE} с ID, {@code REFERENCE_NOT_FOUND} с недостающей ссылкой
     *     или {@code CONFLICT}, если полное название занято
     */
    @Transactional
    public Change create(Organization organization) {
        Optional<Change> refusal = refusal(organization);
        if (refusal.isPresent()) {
            return refusal.get();
        }
        organization.setOfficialAddress(persistOrFind(organization.getOfficialAddress()));
        organization.setPostalAddress(persistOrFind(organization.getPostalAddress()));
        repository.save(organization);
        events.changed();
        return new Change(Outcome.DONE, organization.getId(), null, null);
    }

    /**
     * Ищет организацию.
     *
     * @param id ID организации
     * @return организация или пустой результат
     */
    public Optional<Organization> find(long id) {
        return repository.findById(id);
    }

    /**
     * Возвращает страницу организаций.
     *
     * @param query параметры страницы
     * @param filters параметры запроса с фильтрами контракта
     * @return организации страницы
     */
    public List<Organization> findPage(ListQuery query, Map<String, List<String>> filters) {
        return repository.findPage(query, filters);
    }

    /**
     * Считает организации с учётом фильтров.
     *
     * @param filters параметры запроса с фильтрами контракта
     * @return количество
     */
    public long count(Map<String, List<String>> filters) {
        return repository.count(filters);
    }

    /**
     * Заменяет все поля и адреса организации. Прежние адреса не удаляются.
     *
     * @param id ID организации
     * @param values новые значения
     * @return {@code DONE}, {@code NOT_FOUND}, {@code REFERENCE_NOT_FOUND} с недостающей
     *     ссылкой или {@code CONFLICT}, если название занято другой организацией
     */
    @Transactional
    public Change update(long id, Organization values) {
        Optional<Organization> organization = repository.findById(id);
        if (organization.isEmpty()) {
            return new Change(Outcome.NOT_FOUND, null, null, null);
        }
        Optional<Change> missing = missingReference(values);
        if (missing.isPresent()) {
            return missing.get();
        }
        boolean nameTaken = repository.findByFullName(values.getFullName())
                .filter(other -> other.getId() != id)
                .isPresent();
        if (nameTaken) {
            return new Change(Outcome.CONFLICT, null, null, null);
        }
        Organization target = organization.get();
        target.setOfficialAddress(persistOrFind(values.getOfficialAddress()));
        target.setPostalAddress(persistOrFind(values.getPostalAddress()));
        target.setAnnualTurnover(values.getAnnualTurnover());
        target.setEmployeesCount(values.getEmployeesCount());
        target.setFullName(values.getFullName());
        target.setRating(values.getRating());
        target.setType(values.getType());
        events.changed();
        return new Change(Outcome.DONE, null, null, null);
    }

    /**
     * Удаляет организацию, если на неё не ссылаются работники. Адреса не удаляет.
     *
     * @param id ID организации
     * @return {@code DONE}, {@code NOT_FOUND} или {@code IN_USE}
     */
    @Transactional
    public Outcome delete(long id) {
        Optional<Organization> organization = repository.findById(id);
        if (organization.isEmpty()) {
            return Outcome.NOT_FOUND;
        }
        if (repository.isUsed(id)) {
            return Outcome.IN_USE;
        }
        repository.delete(organization.get());
        events.changed();
        return Outcome.DONE;
    }

    /**
     * Проверяет новую организацию без записи в БД: ссылки на адреса и локации
     * и занятость полного названия.
     *
     * @param organization новая организация
     * @return {@code REFERENCE_NOT_FOUND} с недостающей ссылкой, {@code CONFLICT}
     *     с видом {@code "organization"} или пустой результат, если создать можно
     */
    public Optional<Change> refusal(Organization organization) {
        Optional<Change> missing = missingReference(organization);
        if (missing.isPresent()) {
            return missing;
        }
        return repository.findByFullName(organization.getFullName()).isPresent()
                ? Optional.of(new Change(Outcome.CONFLICT, null, "organization", null))
                : Optional.empty();
    }

    /** Первая ссылка на несуществующий адрес или локацию нового адреса. */
    private Optional<Change> missingReference(Organization organization) {
        return Stream.of(organization.getOfficialAddress(), organization.getPostalAddress())
                .map(this::missingReference)
                .flatMap(Optional::stream)
                .findFirst();
    }

    private Optional<Change> missingReference(Address address) {
        if (address.getId() != null) {
            return addressRepository.findById(address.getId()).isPresent()
                    ? Optional.empty()
                    : Optional.of(missing("address", address.getId()));
        }
        Location town = address.getTown();
        if (town != null && town.getId() != null
                && locationRepository.findById(town.getId()).isEmpty()) {
            return Optional.of(missing("location", town.getId()));
        }
        return Optional.empty();
    }

    private Change missing(String type, long id) {
        return new Change(Outcome.REFERENCE_NOT_FOUND, null, type, id);
    }

    /** Ссылки уже проверены {@link #missingReference(Organization)} в этой транзакции. */
    private Address persistOrFind(Address address) {
        if (address.getId() != null) {
            return addressRepository.findById(address.getId()).orElseThrow();
        }
        Location town = address.getTown();
        if (town != null && town.getId() != null) {
            address.setTown(locationRepository.findById(town.getId()).orElseThrow());
        } else if (town != null) {
            locationRepository.save(town);
        }
        addressRepository.save(address);
        return address;
    }
}
