package com.github.vodobryshkin.islab1.backend.repository;

import com.github.vodobryshkin.islab1.backend.domain.Location;
import com.github.vodobryshkin.islab1.backend.dto.common.ListQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

/**
 * Хранилище локаций.
 */
@ApplicationScoped
public class LocationRepository {
    @PersistenceContext(unitName = "is-lab1")
    private EntityManager entityManager;

    /**
     * Ищет локацию по ID.
     *
     * @param id ID локации
     * @return локация или пустой результат
     */
    public Optional<Location> findById(long id) {
        return Optional.ofNullable(entityManager.find(Location.class, id));
    }

    /**
     * Возвращает страницу локаций.
     *
     * @param query параметры страницы
     * @return локации страницы
     */
    public List<Location> findPage(ListQuery query) {
        long offset = (long) (query.getPage() - 1) * query.getPageSize();
        String column = switch (query.getSortBy()) {
            case "x" -> "l.x";
            case "y" -> "l.y";
            case "z" -> "l.z";
            default -> "l.id";
        };
        String order = query.isAscending() ? "ASC" : "DESC";

        return entityManager.createQuery(
                        "SELECT l FROM Location l ORDER BY " + column + " " + order + ", l.id",
                        Location.class)
                .setFirstResult((int) Math.min(offset, Integer.MAX_VALUE))
                .setMaxResults(query.getPageSize())
                .getResultList();
    }

    /**
     * Считает все локации.
     *
     * @return количество
     */
    public long count() {
        return entityManager.createQuery("SELECT COUNT(l) FROM Location l", Long.class)
                .getSingleResult();
    }

    /**
     * Проверяет, используется ли локация адресами или персонами.
     *
     * @param id ID локации
     * @return true, если локация используется
     */
    public boolean isUsed(long id) {
        return entityManager.createQuery(
                        "SELECT COUNT(a) FROM Address a WHERE a.town.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult()
                + entityManager.createQuery(
                        "SELECT COUNT(p) FROM Person p WHERE p.location.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult() > 0;
    }

    /**
     * Сохраняет новую локацию.
     *
     * @param location локация
     */
    public void save(Location location) {
        entityManager.persist(location);
    }

    /**
     * Удаляет локацию.
     *
     * @param location локация
     */
    public void delete(Location location) {
        entityManager.remove(location);
        entityManager.flush();
    }
}
