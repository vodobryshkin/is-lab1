package com.github.vodobryshkin.islab1.backend.repository;

import com.github.vodobryshkin.islab1.backend.domain.Location;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Хранилище локаций в БД. Транзакциями управляет вызывающий сервис.
 */
@ApplicationScoped
public class LocationRepository {
    private static final Map<String, String> SORT_COLUMNS =
            Map.of("id", "l.id", "x", "l.x", "y", "l.y", "z", "l.z");

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
     * Возвращает страницу локаций; при равных значениях порядок задаёт ID по возрастанию.
     *
     * @param query проверенные параметры страницы
     * @return локации страницы; за концом списка — пустой список
     */
    public List<Location> findPage(ListQuery query) {
        long offset = (long) (query.getPage() - 1) * query.getPageSize();
        if (offset > Integer.MAX_VALUE) {
            return List.of();
        }
        String order = SORT_COLUMNS.get(query.getSortBy())
                + (query.isAscending() ? " ASC" : " DESC");
        return entityManager.createQuery(
                        "SELECT l FROM Location l ORDER BY " + order + ", l.id ASC",
                        Location.class)
                .setFirstResult((int) offset)
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
     * Проверяет, ссылается ли на локацию адрес или персона.
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
     * Сохраняет новую локацию; ID выдаёт последовательность БД.
     *
     * @param location локация без ID
     */
    public void save(Location location) {
        entityManager.persist(location);
    }

    /**
     * Удаляет локацию и сразу отправляет DELETE: внешний ключ БД отклонит удаление,
     * если адрес или персона сослались на локацию после проверки isUsed.
     *
     * @param location сохранённая локация
     */
    public void delete(Location location) {
        entityManager.remove(location);
        entityManager.flush();
    }
}
