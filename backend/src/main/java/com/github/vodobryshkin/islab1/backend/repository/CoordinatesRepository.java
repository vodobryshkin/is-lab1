package com.github.vodobryshkin.islab1.backend.repository;

import com.github.vodobryshkin.islab1.backend.domain.Coordinates;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

/**
 * Хранилище координат.
 */
@ApplicationScoped
public class CoordinatesRepository {
    @PersistenceContext(unitName = "is-lab1")
    private EntityManager entityManager;

    /**
     * Ищет координаты по ID.
     *
     * @param id ID координат
     * @return координаты или пустой результат
     */
    public Optional<Coordinates> findById(long id) {
        return Optional.ofNullable(entityManager.find(Coordinates.class, id));
    }

    /**
     * Возвращает страницу координат.
     *
     * @param query параметры страницы
     * @return координаты страницы
     */
    public List<Coordinates> findPage(ListQuery query) {
        long offset = (long) (query.getPage() - 1) * query.getPageSize();
        String column = switch (query.getSortBy()) {
            case "x" -> "c.x";
            case "y" -> "c.y";
            default -> "c.id";
        };
        String order = query.isAscending() ? "ASC" : "DESC";

        return entityManager.createQuery(
                        "SELECT c FROM Coordinates c ORDER BY " + column + " " + order + ", c.id",
                        Coordinates.class)
                .setFirstResult((int) Math.min(offset, Integer.MAX_VALUE))
                .setMaxResults(query.getPageSize())
                .getResultList();
    }

    /**
     * Считает все координаты.
     *
     * @return количество
     */
    public long count() {
        return entityManager.createQuery("SELECT COUNT(c) FROM Coordinates c", Long.class)
                .getSingleResult();
    }

    /**
     * Проверяет, используются ли координаты работниками.
     *
     * @param id ID координат
     * @return true, если координаты используются
     */
    public boolean isUsed(long id) {
        return entityManager.createQuery(
                        "SELECT COUNT(w) FROM Worker w WHERE w.coordinates.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult() > 0;
    }

    /**
     * Сохраняет новые координаты.
     *
     * @param coordinates координаты
     */
    public void save(Coordinates coordinates) {
        entityManager.persist(coordinates);
    }

    /**
     * Удаляет координаты.
     *
     * @param coordinates координаты
     */
    public void delete(Coordinates coordinates) {
        entityManager.remove(coordinates);
        entityManager.flush();
    }
}
