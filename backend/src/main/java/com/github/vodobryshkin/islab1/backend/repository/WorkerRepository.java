package com.github.vodobryshkin.islab1.backend.repository;

import com.github.vodobryshkin.islab1.backend.domain.Position;
import com.github.vodobryshkin.islab1.backend.domain.Status;
import com.github.vodobryshkin.islab1.backend.domain.Worker;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Хранилище работников.
 */
@ApplicationScoped
public class WorkerRepository {
    @PersistenceContext(unitName = "is-lab1")
    private EntityManager entityManager;

    /**
     * Ищет работника по ID.
     *
     * @param id ID работника
     * @return работник или пустой результат
     */
    public Optional<Worker> findById(int id) {
        return Optional.ofNullable(entityManager.find(Worker.class, id));
    }

    /**
     * Возвращает страницу работников с фильтрами по подстроке.
     *
     * @param query параметры страницы
     * @param name подстрока имени или null
     * @param position подстрока должности или null
     * @param status подстрока статуса или null
     * @return работники страницы
     */
    public List<Worker> findPage(ListQuery query, String name, String position, String status) {
        String nameText = name == null ? "" : name.toLowerCase(Locale.ROOT);
        String positionText = position == null ? "" : position.toLowerCase(Locale.ROOT);
        String statusText = status == null ? "" : status.toLowerCase(Locale.ROOT);
        List<Position> positions = Stream.of(Position.values())
                .filter(value -> value.name().toLowerCase(Locale.ROOT).contains(positionText))
                .toList();
        List<Status> statuses = Stream.of(Status.values())
                .filter(value -> value.name().toLowerCase(Locale.ROOT).contains(statusText))
                .toList();

        if (positions.isEmpty() || statuses.isEmpty()) {
            return List.of();
        }

        long offset = (long) (query.getPage() - 1) * query.getPageSize();
        String column = switch (query.getSortBy()) {
            case "name" -> "LOWER(w.name)";
            case "salary" -> "w.salary";
            case "rating" -> "w.rating";
            case "start_date" -> "w.startDate";
            case "creation_date" -> "w.creationDate";
            case "position" -> "w.position";
            case "status" -> "w.status";
            default -> "w.id";
        };
        String order = query.isAscending() ? "ASC" : "DESC";

        return entityManager.createQuery("SELECT w FROM Worker w"
                        + " WHERE LOCATE(:nameText, LOWER(w.name)) > 0"
                        + " AND (:positionText = '' OR w.position IN :positions)"
                        + " AND (:statusText = '' OR w.status IN :statuses)"
                        + " ORDER BY " + column + " " + order + " NULLS LAST, w.id",
                        Worker.class)
                .setParameter("nameText", nameText)
                .setParameter("positionText", positionText)
                .setParameter("positions", positions)
                .setParameter("statusText", statusText)
                .setParameter("statuses", statuses)
                .setFirstResult((int) Math.min(offset, Integer.MAX_VALUE))
                .setMaxResults(query.getPageSize())
                .getResultList();
    }

    /**
     * Считает работников с фильтрами по подстроке.
     *
     * @param name подстрока имени или null
     * @param position подстрока должности или null
     * @param status подстрока статуса или null
     * @return количество
     */
    public long count(String name, String position, String status) {
        String nameText = name == null ? "" : name.toLowerCase(Locale.ROOT);
        String positionText = position == null ? "" : position.toLowerCase(Locale.ROOT);
        String statusText = status == null ? "" : status.toLowerCase(Locale.ROOT);
        List<Position> positions = Stream.of(Position.values())
                .filter(value -> value.name().toLowerCase(Locale.ROOT).contains(positionText))
                .toList();
        List<Status> statuses = Stream.of(Status.values())
                .filter(value -> value.name().toLowerCase(Locale.ROOT).contains(statusText))
                .toList();

        if (positions.isEmpty() || statuses.isEmpty()) {
            return 0;
        }

        return entityManager.createQuery("SELECT COUNT(w) FROM Worker w"
                        + " WHERE LOCATE(:nameText, LOWER(w.name)) > 0"
                        + " AND (:positionText = '' OR w.position IN :positions)"
                        + " AND (:statusText = '' OR w.status IN :statuses)", Long.class)
                .setParameter("nameText", nameText)
                .setParameter("positionText", positionText)
                .setParameter("positions", positions)
                .setParameter("statusText", statusText)
                .setParameter("statuses", statuses)
                .getSingleResult();
    }

    /**
     * Считает работников с указанным статусом.
     *
     * @param status статус или null
     * @return количество
     */
    public long countByStatus(Status status) {
        if (status == null) {
            return entityManager.createQuery(
                            "SELECT COUNT(w) FROM Worker w WHERE w.status IS NULL", Long.class)
                    .getSingleResult();
        }

        return entityManager.createQuery(
                        "SELECT COUNT(w) FROM Worker w WHERE w.status = :status", Long.class)
                .setParameter("status", status)
                .getSingleResult();
    }

    /**
     * Считает работников с одним из указанных статусов.
     *
     * @param statuses статусы
     * @return количество
     */
    public long countByStatusIn(List<Status> statuses) {
        return entityManager.createQuery(
                        "SELECT COUNT(w) FROM Worker w WHERE w.status IN :statuses", Long.class)
                .setParameter("statuses", statuses)
                .getSingleResult();
    }

    /**
     * Возвращает работников с рейтингом меньше заданного.
     *
     * @param rating рейтинг
     * @return работники
     */
    public List<Worker> findByRatingLessThan(int rating) {
        return entityManager.createQuery(
                        "SELECT w FROM Worker w WHERE w.rating < :rating ORDER BY w.id",
                        Worker.class)
                .setParameter("rating", rating)
                .getResultList();
    }

    /**
     * Возвращает работников организации и блокирует их до конца транзакции.
     *
     * @param organizationId ID организации
     * @return работники организации
     */
    public List<Worker> findByOrganizationForUpdate(long organizationId) {
        return entityManager.createQuery(
                        "SELECT w FROM Worker w WHERE w.organization.id = :id ORDER BY w.id",
                        Worker.class)
                .setParameter("id", organizationId)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultList();
    }

    /**
     * Проверяет, связана ли с работником персона.
     *
     * @param id ID работника
     * @return true, если персона есть
     */
    public boolean isReferencedByPerson(int id) {
        return entityManager.createQuery(
                        "SELECT COUNT(p) FROM Person p WHERE p.worker.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult() > 0;
    }

    /**
     * Сохраняет нового работника.
     *
     * @param worker работник
     */
    public void save(Worker worker) {
        entityManager.persist(worker);
        entityManager.flush();
    }

    /**
     * Удаляет работника.
     *
     * @param worker работник
     */
    public void delete(Worker worker) {
        entityManager.remove(worker);
        entityManager.flush();
    }
}
