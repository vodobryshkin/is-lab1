package com.github.vodobryshkin.islab1.backend.service;

import com.github.vodobryshkin.islab1.backend.domain.Organization;
import com.github.vodobryshkin.islab1.backend.domain.Status;
import com.github.vodobryshkin.islab1.backend.domain.Worker;
import com.github.vodobryshkin.islab1.backend.events.EventSessions;
import com.github.vodobryshkin.islab1.backend.repository.OrganizationRepository;
import com.github.vodobryshkin.islab1.backend.repository.WorkerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Специальные операции ТЗ над работниками. Отделены от WorkerService, чтобы
 * CRUD и операции ТЗ не собирались в одном классе.
 */
@ApplicationScoped
public class SpecialOperationService {
    /** Порядок статусов из контракта; имена констант для сравнения не используются. */
    private static final List<Status> STATUS_ORDER =
            List.of(Status.HIRED, Status.RECOMMENDED_FOR_PROMOTION, Status.REGULAR);

    private final WorkerRepository workerRepository;
    private final OrganizationRepository organizationRepository;
    private final EventSessions events;

    /**
     * Создаёт сервис.
     *
     * @param workerRepository хранилище работников
     * @param organizationRepository хранилище организаций
     * @param events уведомление об изменениях после фиксации транзакции
     */
    @Inject
    public SpecialOperationService(WorkerRepository workerRepository,
            OrganizationRepository organizationRepository, EventSessions events) {
        this.workerRepository = workerRepository;
        this.organizationRepository = organizationRepository;
        this.events = events;
    }

    /** Нужен CDI для создания клиентского прокси; прикладной код его не вызывает. */
    protected SpecialOperationService() {
        this(null, null, null);
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
        Optional<Worker> worker = workerRepository.findById(workerId);
        if (worker.isEmpty()) {
            return Outcome.NOT_FOUND;
        }
        Optional<Organization> organization = organizationRepository.findById(organizationId);
        if (organization.isEmpty()) {
            return Outcome.REFERENCE_NOT_FOUND;
        }
        Worker target = worker.get();
        if (target.getStatus() != Status.HIRED || target.getOrganization() == null
                || !target.getOrganization().getId().equals(organizationId)) {
            target.setOrganization(organization.get());
            target.setStatus(Status.HIRED);
            events.changed();
        }
        return Outcome.DONE;
    }

    /**
     * Умножает зарплаты работников организации на коэффициент.
     * Если хотя бы одна зарплата перестала бы быть конечной и положительной, ни одна не меняется.
     *
     * @param organizationId ID организации
     * @param coefficient положительный конечный коэффициент
     * @return DONE с числом проиндексированных работников в id (0 для
     *     организации без работников), NOT_FOUND или CONFLICT
     */
    @Transactional
    public Change indexSalaries(long organizationId, double coefficient) {
        if (organizationRepository.findById(organizationId).isEmpty()) {
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
     * Считает работников с указанным статусом.
     *
     * @param status статус; null — работники без статуса
     * @return количество
     */
    public long countByStatus(Status status) {
        return workerRepository.countByStatus(status);
    }

    /**
     * Считает работников со статусом выше заданного в порядке
     * HIRED < RECOMMENDED_FOR_PROMOTION < REGULAR; без статуса не учитываются.
     *
     * @param status статус для сравнения
     * @return количество
     */
    public long countByStatusGreaterThan(Status status) {
        List<Status> greater =
                STATUS_ORDER.subList(STATUS_ORDER.indexOf(status) + 1, STATUS_ORDER.size());
        return greater.isEmpty() ? 0 : workerRepository.countByStatusIn(greater);
    }

    /**
     * Возвращает всех работников с рейтингом строго меньше порога.
     *
     * @param rating порог
     * @return работники по возрастанию ID
     */
    public List<Worker> findByRatingLessThan(int rating) {
        return workerRepository.findByRatingLessThan(rating);
    }
}
