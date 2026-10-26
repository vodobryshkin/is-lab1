package com.github.vodobryshkin.islab1.backend.service;

import com.github.vodobryshkin.islab1.backend.domain.Organization;
import com.github.vodobryshkin.islab1.backend.domain.Status;
import com.github.vodobryshkin.islab1.backend.domain.Worker;
import com.github.vodobryshkin.islab1.backend.repository.OrganizationRepository;
import com.github.vodobryshkin.islab1.backend.repository.WorkerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Специальные операции ТЗ над работниками. Отделены от {@link WorkerService}, чтобы
 * CRUD и операции ТЗ не собирались в одном классе.
 */
@ApplicationScoped
public class SpecialOperationService {
    /** Порядок статусов из контракта; имена констант для сравнения не используются. */
    private static final List<Status> STATUS_ORDER =
            List.of(Status.HIRED, Status.RECOMMENDED_FOR_PROMOTION, Status.REGULAR);

    private final WorkerRepository workerRepository;
    private final OrganizationRepository organizationRepository;

    /**
     * Создаёт сервис.
     *
     * @param workerRepository хранилище работников
     * @param organizationRepository хранилище организаций
     */
    @Inject
    public SpecialOperationService(WorkerRepository workerRepository,
            OrganizationRepository organizationRepository) {
        this.workerRepository = workerRepository;
        this.organizationRepository = organizationRepository;
    }

    /** Нужен CDI для создания клиентского прокси; прикладной код его не вызывает. */
    protected SpecialOperationService() {
        this(null, null);
    }

    /**
     * Принимает работника в организацию: в одной транзакции назначает её и статус
     * {@code HIRED}, заменяя прежнюю привязку. Повторный вызов с той же организацией
     * ничего не меняет и тоже успешен. {@code employees_count} не пересчитывается:
     * контракт этого не требует.
     *
     * @param workerId ID работника
     * @param organizationId ID существующей организации
     * @return {@code DONE}, {@code NOT_FOUND} без работника или
     *     {@code REFERENCE_NOT_FOUND} без организации; при отказе ничего не меняется
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
        worker.get().setOrganization(organization.get());
        worker.get().setStatus(Status.HIRED);
        return Outcome.DONE;
    }

    /**
     * Считает работников с указанным статусом.
     *
     * @param status статус; {@code null} — работники без статуса
     * @return количество
     */
    public long countByStatus(Status status) {
        return workerRepository.countByStatus(status);
    }

    /**
     * Считает работников со статусом выше заданного в порядке
     * {@code HIRED < RECOMMENDED_FOR_PROMOTION < REGULAR}; без статуса не учитываются.
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
