package com.github.vodobryshkin.islab1.backend.service;

import com.github.vodobryshkin.islab1.backend.domain.Address;
import com.github.vodobryshkin.islab1.backend.domain.Location;
import com.github.vodobryshkin.islab1.backend.domain.Organization;
import com.github.vodobryshkin.islab1.backend.domain.Worker;
import com.github.vodobryshkin.islab1.backend.dto.common.ListQuery;
import com.github.vodobryshkin.islab1.backend.events.EventSessions;
import com.github.vodobryshkin.islab1.backend.repository.AddressRepository;
import com.github.vodobryshkin.islab1.backend.repository.LocationRepository;
import com.github.vodobryshkin.islab1.backend.repository.OrganizationRepository;
import com.github.vodobryshkin.islab1.backend.repository.WorkerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Операции с организациями.
 * Все ссылки проверяются до записи, поэтому при отказе не остаётся частично созданных адресов.
 */
@ApplicationScoped
public class OrganizationService {
    private final OrganizationRepository repository;
    private final AddressRepository addressRepository;
    private final LocationRepository locationRepository;
    private final WorkerRepository workerRepository;
    private final EventSessions events;

    /**
     * Создаёт сервис.
     *
     * @param repository хранилище организаций
     * @param addressRepository хранилище адресов
     * @param locationRepository хранилище локаций
     * @param workerRepository хранилище работников
     * @param events уведомление об изменениях после фиксации транзакции
     */
    @Inject
    public OrganizationService(OrganizationRepository repository,
            AddressRepository addressRepository, LocationRepository locationRepository,
            WorkerRepository workerRepository, EventSessions events) {
        this.repository = repository;
        this.addressRepository = addressRepository;
        this.locationRepository = locationRepository;
        this.workerRepository = workerRepository;
        this.events = events;
    }

    /**
     * Нужен CDI для создания клиентского прокси, прикладной код его не вызывает.
     */
    protected OrganizationService() {
        this.repository = null;
        this.addressRepository = null;
        this.locationRepository = null;
        this.workerRepository = null;
        this.events = null;
    }

    /**
     * Сохраняет организацию вместе с новыми адресами и локациями в одной транзакции.
     *
     * @param organization организация, у ссылки на адрес или локацию задан только ID
     * @return DONE, REFERENCE_NOT_FOUND, CONFLICT
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
     * Возвращает страницу организаций с фильтрами по подстроке.
     *
     * @param query параметры страницы
     * @param fullName подстрока полного названия или null
     * @param type подстрока типа или null
     * @return организации страницы
     */
    public List<Organization> findPage(ListQuery query, String fullName, String type) {
        return repository.findPage(query, fullName, type);
    }

    /**
     * Считает организации с фильтрами по подстроке.
     *
     * @param fullName подстрока полного названия или null
     * @param type подстрока типа или null
     * @return количество
     */
    public long count(String fullName, String type) {
        return repository.count(fullName, type);
    }

    /**
     * Заменяет все поля и адреса организации. Прежние адреса не удаляются.
     *
     * @param id ID организации
     * @param values новые значения
     * @return DONE, NOT_FOUND, REFERENCE_NOT_FOUND, CONFLICT
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
     * Умножает зарплаты работников организации на коэффициент.
     * Если хотя бы одна зарплата перестала бы быть конечной и положительной, ни одна не меняется.
     *
     * @param organizationId ID организации
     * @param coefficient положительный конечный коэффициент
     * @return DONE, NOT_FOUND, CONFLICT
     */
    @Transactional
    public Change indexSalaries(long organizationId, double coefficient) {
        if (repository.findById(organizationId).isEmpty()) {
            return new Change(Outcome.NOT_FOUND, null, null, null);
        }

        List<Worker> workers = workerRepository.findByOrganizationForUpdate(organizationId);
        double[] salaries = new double[workers.size()];

        for (int i = 0; i < salaries.length; i++) {
            salaries[i] = workers.get(i).getSalary() * coefficient;
            if (salaries[i] <= 0 || Double.isInfinite(salaries[i])) {
                return new Change(Outcome.CONFLICT, null, null, null);
            }
        }

        for (int i = 0; i < salaries.length; i++) {
            if (Double.compare(workers.get(i).getSalary(), salaries[i]) != 0) {
                workers.get(i).setSalary(salaries[i]);
                events.changed();
            }
        }

        return new Change(Outcome.DONE, (long) workers.size(), null, null);
    }

    /**
     * Удаляет организацию, если на неё не ссылаются работники. Адреса не удаляет.
     *
     * @param id ID организации
     * @return DONE, NOT_FOUND, IN_USE
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
     * @return REFERENCE_NOT_FOUND, CONFLICT или пустой результат
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

    /**
     * Первая ссылка на несуществующий адрес или локацию нового адреса.
     */
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

    /**
     * Ссылки уже проверены missingReference в этой транзакции.
     */
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
