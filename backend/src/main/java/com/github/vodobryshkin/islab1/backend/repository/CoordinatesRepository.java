package com.github.vodobryshkin.islab1.backend.repository;

import com.github.vodobryshkin.islab1.backend.domain.Coordinates;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Хранилище координат в БД. Транзакциями управляет вызывающий сервис.
 */
@ApplicationScoped
public class CoordinatesRepository {
    private static final Map<String, String> SORT_COLUMNS =
            Map.of("id", "c.id", "x", "c.x", "y", "c.y");

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
     * Возвращает страницу координат, при равных значениях порядок задаёт ID по возрастанию.
     *
     * @param query проверенные параметры страницы
     * @return координаты страницы, за концом списка -- пустой список
     */
    public List<Coordinates> findPage(ListQuery query) {
        long offset = (long) (query.getPage() - 1) * query.getPageSize();

        if (offset > Integer.MAX_VALUE) {
            return List.of();
        }

        String order = SORT_COLUMNS.get(query.getSortBy())
                + (query.isAscending() ? " ASC" : " DESC");

        return entityManager.createQuery(
                        "SELECT c FROM Coordinates c ORDER BY " + order + ", c.id ASC",
                        Coordinates.class)
                .setFirstResult((int) offset)
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
     * Проверяет, ссылается ли на координаты хотя бы один работник.
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
     * Сохраняет новые координаты, ID выдаёт последовательность БД.
     *
     * @param coordinates координаты без ID
     */
    public void save(Coordinates coordinates) {
        entityManager.persist(coordinates);
    }

    /**
     * Удаляет координаты и сразу отправляет DELETE: внешний ключ БД отклонит удаление,
     * если работник сослался на координаты после проверки isUsed.
     *
     * @param coordinates сохранённые координаты
     */
    public void delete(Coordinates coordinates) {
        entityManager.remove(coordinates);
        entityManager.flush();
    }
}
