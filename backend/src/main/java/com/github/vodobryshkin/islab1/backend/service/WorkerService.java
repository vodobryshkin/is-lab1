package com.github.vodobryshkin.islab1.backend.service;

import com.github.vodobryshkin.islab1.backend.domain.Coordinates;
import com.github.vodobryshkin.islab1.backend.domain.Location;
import com.github.vodobryshkin.islab1.backend.domain.Organization;
import com.github.vodobryshkin.islab1.backend.domain.Person;
import com.github.vodobryshkin.islab1.backend.domain.Status;
import com.github.vodobryshkin.islab1.backend.domain.Worker;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import com.github.vodobryshkin.islab1.backend.events.EventSessions;
import com.github.vodobryshkin.islab1.backend.repository.CoordinatesRepository;
import com.github.vodobryshkin.islab1.backend.repository.LocationRepository;
import com.github.vodobryshkin.islab1.backend.repository.PersonRepository;
import com.github.vodobryshkin.islab1.backend.repository.UserRepository;
import com.github.vodobryshkin.islab1.backend.repository.WorkerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Операции с работниками.
 * Все ссылки и конфликты проверяются до первой записи, поэтому при отказе не остаётся частично
 * созданных объектов.
 */
@ApplicationScoped
public class WorkerService {
    private final WorkerRepository repository;
    private final UserRepository userRepository;
    private final CoordinatesRepository coordinatesRepository;
    private final PersonRepository personRepository;
    private final LocationRepository locationRepository;
    private final OrganizationService organizationService;
    private final EventSessions events;

    /**
     * Создаёт сервис.
     *
     * @param repository хранилище работников
     * @param userRepository хранилище пользователей
     * @param coordinatesRepository хранилище координат
     * @param personRepository хранилище персон
     * @param locationRepository хранилище локаций
     * @param organizationService поиск, проверка и создание организации
     * @param events уведомление об изменениях после фиксации транзакции
     */
    @Inject
    public WorkerService(WorkerRepository repository, UserRepository userRepository,
            CoordinatesRepository coordinatesRepository, PersonRepository personRepository,
            LocationRepository locationRepository, OrganizationService organizationService,
            EventSessions events) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.coordinatesRepository = coordinatesRepository;
        this.personRepository = personRepository;
        this.locationRepository = locationRepository;
        this.organizationService = organizationService;
        this.events = events;
    }

    /**
     * Нужен CDI для создания клиентского прокси; прикладной код его не вызывает.
     */
    protected WorkerService() {
        this(null, null, null, null, null, null, null);
    }

    /**
     * Создаёт работника вместе с новыми вложенными объектами в одной транзакции.
     * Создатель -- вошедший пользователь, дату создания задаёт сервер.
     *
     * @param worker работник; у ссылок на координаты, персону и организацию задан только ID
     * @param username логин вошедшего пользователя
     * @return DONE с ID работника; REFERENCE_NOT_FOUND с недостающей
     *     ссылкой; CONFLICT с видом "organization" (название занято)
     *     или "person" (персона уже связана с работником)
     */
    @Transactional
    public Change create(Worker worker, String username) {
        Optional<Change> refusal = refusal(worker, null);
        if (refusal.isPresent()) {
            return refusal.get();
        }
        worker.setUser(userRepository.findByUsername(username).orElseThrow());
        worker.setCoordinates(persistOrFind(worker.getCoordinates()));
        worker.setOrganization(persistOrFind(worker.getOrganization()));
        Person person = worker.getPerson();
        worker.setPerson(null);
        repository.save(worker);
        attach(worker, person);
        events.changed();
        return new Change(Outcome.DONE, worker.getId().longValue(), null, null);
    }

    /**
     * Заменяет поля и привязки работника; создатель и дата создания не меняются.
     * Прежняя персона отвязывается, но не удаляется.
     *
     * @param id ID работника
     * @param values новые значения; организация -- только ссылка или null
     * @return DONE; NOT_FOUND; REFERENCE_NOT_FOUND с недостающей
     *     ссылкой; CONFLICT с видом "person", если персона связана
     *     с другим работником
     */
    @Transactional
    public Change update(int id, Worker values) {
        Optional<Worker> found = repository.findById(id);
        if (found.isEmpty()) {
            return new Change(Outcome.NOT_FOUND, null, null, null);
        }
        Optional<Change> refusal = refusal(values, id);
        if (refusal.isPresent()) {
            return refusal.get();
        }
        Worker target = found.get();
        target.setName(values.getName());
        target.setCoordinates(persistOrFind(values.getCoordinates()));
        target.setOrganization(persistOrFind(values.getOrganization()));
        target.setSalary(values.getSalary());
        target.setRating(values.getRating());
        target.setStartDate(values.getStartDate());
        target.setPosition(values.getPosition());
        target.setStatus(values.getStatus());
        Person current = target.getPerson();
        Person next = values.getPerson();
        if (current != null && (next == null || !current.getId().equals(next.getId()))) {
            personRepository.unlink(current);
            target.setPerson(null);
        }
        attach(target, next);
        events.changed();
        return new Change(Outcome.DONE, (long) id, null, null);
    }

    /**
     * Удаляет работника, если с ним не связана персона.
     *
     * @param id ID работника
     * @return DONE, NOT_FOUND или IN_USE
     */
    @Transactional
    public Outcome delete(int id) {
        Optional<Worker> worker = repository.findById(id);
        if (worker.isEmpty()) {
            return Outcome.NOT_FOUND;
        }
        if (repository.isReferencedByPerson(id)) {
            return Outcome.IN_USE;
        }
        repository.delete(worker.get());
        events.changed();
        return Outcome.DONE;
    }

    /**
     * Назначает работнику существующую организацию и статус HIRED.
     *
     * @param workerId ID работника
     * @param organizationId ID существующей организации
     * @return DONE, NOT_FOUND без работника или
     *     REFERENCE_NOT_FOUND без организации; при отказе ничего не меняется
     */
    @Transactional
    public Outcome hire(int workerId, long organizationId) {
        Optional<Worker> worker = repository.findById(workerId);
        if (worker.isEmpty()) {
            return Outcome.NOT_FOUND;
        }
        Organization organization = organizationService.find(organizationId).orElse(null);
        if (organization == null) {
            return Outcome.REFERENCE_NOT_FOUND;
        }
        Worker target = worker.get();
        if (target.getStatus() != Status.HIRED || target.getOrganization() == null
                || !target.getOrganization().getId().equals(organizationId)) {
            target.setOrganization(organization);
            target.setStatus(Status.HIRED);
            events.changed();
        }
        return Outcome.DONE;
    }

    /**
     * Считает работников с указанным статусом.
     *
     * @param status статус; null -- работники без статуса
     * @return количество
     */
    public long countByStatus(Status status) {
        return repository.countByStatus(status);
    }

    /**
     * Считает работников со статусом выше заданного в порядке
     * HIRED < RECOMMENDED_FOR_PROMOTION < REGULAR; без статуса не учитываются.
     *
     * @param status статус для сравнения
     * @return количество
     */
    public long countByStatusGreaterThan(Status status) {
        return switch (status) {
            case HIRED -> repository.countByStatusIn(
                    List.of(Status.RECOMMENDED_FOR_PROMOTION, Status.REGULAR));
            case RECOMMENDED_FOR_PROMOTION -> repository.countByStatusIn(List.of(Status.REGULAR));
            case REGULAR -> 0;
        };
    }

    /**
     * Возвращает всех работников с рейтингом строго меньше порога.
     *
     * @param rating порог
     * @return работники по возрастанию ID
     */
    public List<Worker> findByRatingLessThan(int rating) {
        return repository.findByRatingLessThan(rating);
    }

    /**
     * Ищет работника.
     *
     * @param id ID работника
     * @return работник с вложенными объектами или пустой результат
     */
    public Optional<Worker> find(int id) {
        return repository.findById(id);
    }

    /**
     * Возвращает страницу работников с вложенными объектами.
     *
     * @param query страница и сортировка
     * @param filters фильтры-подстроки из параметров запроса
     * @return работники страницы
     */
    public List<Worker> findPage(ListQuery query, Map<String, List<String>> filters) {
        return repository.findPage(query, filters);
    }

    /**
     * Считает работников, прошедших фильтры.
     *
     * @param filters фильтры-подстроки из параметров запроса
     * @return количество
     */
    public long count(Map<String, List<String>> filters) {
        return repository.count(filters);
    }

    /**
     * Первая причина отказа: ненайденная ссылка или конфликт; без записи в БД.
     * owner -- ID обновляемого работника, его собственная персона не конфликтует.
     */
    private Optional<Change> refusal(Worker worker, Integer owner) {
        Coordinates coordinates = worker.getCoordinates();
        if (coordinates.getId() != null
                && coordinatesRepository.findById(coordinates.getId()).isEmpty()) {
            return Optional.of(missing("coordinates", coordinates.getId()));
        }
        Optional<Change> organization = organizationRefusal(worker.getOrganization());
        return organization.isPresent()
                ? organization
                : personRefusal(worker.getPerson(), owner);
    }

    private Optional<Change> organizationRefusal(Organization organization) {
        if (organization == null) {
            return Optional.empty();
        }
        if (organization.getId() == null) {
            return organizationService.refusal(organization);
        }
        return organizationService.find(organization.getId()).isPresent()
                ? Optional.empty()
                : Optional.of(missing("organization", organization.getId()));
    }

    /**
     * Существующая персона блокируется до конца транзакции: одновременные запросы
     * не привяжут её к двум работникам.
     */
    private Optional<Change> personRefusal(Person person, Integer owner) {
        if (person == null) {
            return Optional.empty();
        }
        if (person.getId() == null) {
            Location location = person.getLocation();
            return location.getId() != null
                    && locationRepository.findById(location.getId()).isEmpty()
                    ? Optional.of(missing("location", location.getId()))
                    : Optional.empty();
        }
        Person existing = personRepository.findByIdForUpdate(person.getId()).orElse(null);
        if (existing == null) {
            return Optional.of(missing("person", person.getId()));
        }
        Worker holder = existing.getWorker();
        return holder == null || holder.getId().equals(owner)
                ? Optional.empty()
                : Optional.of(new Change(Outcome.CONFLICT, null, "person", person.getId()));
    }

    private Change missing(String subject, long id) {
        return new Change(Outcome.REFERENCE_NOT_FOUND, null, subject, id);
    }

    /**
     * Ссылки уже проверены refusal в этой транзакции.
     */
    private Coordinates persistOrFind(Coordinates coordinates) {
        if (coordinates.getId() != null) {
            return coordinatesRepository.findById(coordinates.getId()).orElseThrow();
        }
        coordinatesRepository.save(coordinates);
        return coordinates;
    }

    private Organization persistOrFind(Organization organization) {
        if (organization == null || organization.getId() != null) {
            return organization == null
                    ? null
                    : organizationService.find(organization.getId()).orElseThrow();
        }
        organizationService.create(organization);
        return organization;
    }

    /**
     * Привязка задаётся со стороны персоны: внешний ключ -- persons.worker_id.
     */
    private void attach(Worker worker, Person person) {
        if (person == null) {
            return;
        }
        Person target = person.getId() == null
                ? person
                : personRepository.findById(person.getId()).orElseThrow();
        if (target.getId() == null) {
            Location location = target.getLocation();
            target.setLocation(location.getId() == null
                    ? location
                    : locationRepository.findById(location.getId()).orElseThrow());
            if (location.getId() == null) {
                locationRepository.save(location);
            }
            target.setWorker(worker);
            personRepository.save(target);
        } else {
            target.setWorker(worker);
        }
        worker.setPerson(target);
    }
}
