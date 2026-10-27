package com.github.vodobryshkin.islab1.backend.repository;

import com.github.vodobryshkin.islab1.backend.domain.Color;
import com.github.vodobryshkin.islab1.backend.domain.Country;
import com.github.vodobryshkin.islab1.backend.domain.Person;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Хранилище персон в БД. Транзакциями управляет вызывающий сервис.
 */
@ApplicationScoped
public class PersonRepository {
    private static final Map<String, String> SORT_COLUMNS = Map.of(
            "id", "p.id",
            "eye_color", "p.eyeColor",
            "hair_color", "p.hairColor",
            "nationality", "p.nationality",
            "birthday", "p.birthday",
            "weight", "p.weight");

    @PersistenceContext(unitName = "is-lab1")
    private EntityManager entityManager;

    /**
     * Ищет персону по ID.
     *
     * @param id ID персоны
     * @return персона или пустой результат
     */
    public Optional<Person> findById(long id) {
        return Optional.ofNullable(entityManager.find(Person.class, id));
    }

    /**
     * Ищет персону и блокирует её строку до конца транзакции, чтобы одновременные
     * запросы не привязали одну персону к двум работникам.
     *
     * @param id ID персоны
     * @return персона или пустой результат
     */
    public Optional<Person> findByIdForUpdate(long id) {
        return Optional.ofNullable(
                entityManager.find(Person.class, id, LockModeType.PESSIMISTIC_WRITE));
    }

    /**
     * Возвращает страницу персон, прошедших фильтры-подстроки по цветам и национальности.
     * null при сортировке идёт последним, при равных значениях порядок задаёт ID.
     *
     * @param query проверенные параметры страницы
     * @param filters параметры запроса с фильтрами контракта
     * @return персоны страницы; за концом списка — пустой список
     */
    public List<Person> findPage(ListQuery query, Map<String, List<String>> filters) {
        long offset = (long) (query.getPage() - 1) * query.getPageSize();
        String eyeColor = filter(filters, "eye_color");
        String hairColor = filter(filters, "hair_color");
        String nationality = filter(filters, "nationality");
        List<Color> eyeColors = matching(Color.values(), eyeColor);
        List<Color> hairColors = matching(Color.values(), hairColor);
        List<Country> nationalities = matching(Country.values(), nationality);
        if (offset > Integer.MAX_VALUE || eyeColors.isEmpty() || hairColors.isEmpty()
                || nationalities.isEmpty()) {
            return List.of();
        }
        String order = SORT_COLUMNS.get(query.getSortBy())
                + (query.isAscending() ? " ASC" : " DESC");
        return entityManager.createQuery("SELECT p FROM Person p"
                        + " WHERE (:eyeColorText = '' OR p.eyeColor IN :eyeColors)"
                        + " AND (:hairColorText = '' OR p.hairColor IN :hairColors)"
                        + " AND (:nationalityText = '' OR p.nationality IN :nationalities)"
                        + " ORDER BY " + order + " NULLS LAST, p.id ASC", Person.class)
                .setParameter("eyeColorText", eyeColor)
                .setParameter("eyeColors", eyeColors)
                .setParameter("hairColorText", hairColor)
                .setParameter("hairColors", hairColors)
                .setParameter("nationalityText", nationality)
                .setParameter("nationalities", nationalities)
                .setFirstResult((int) offset)
                .setMaxResults(query.getPageSize())
                .getResultList();
    }

    /**
     * Считает персоны, прошедшие фильтры-подстроки по цветам и национальности.
     *
     * @param filters параметры запроса с фильтрами контракта
     * @return количество
     */
    public long count(Map<String, List<String>> filters) {
        String eyeColor = filter(filters, "eye_color");
        String hairColor = filter(filters, "hair_color");
        String nationality = filter(filters, "nationality");
        List<Color> eyeColors = matching(Color.values(), eyeColor);
        List<Color> hairColors = matching(Color.values(), hairColor);
        List<Country> nationalities = matching(Country.values(), nationality);
        if (eyeColors.isEmpty() || hairColors.isEmpty() || nationalities.isEmpty()) {
            return 0;
        }
        return entityManager.createQuery("SELECT COUNT(p) FROM Person p"
                        + " WHERE (:eyeColorText = '' OR p.eyeColor IN :eyeColors)"
                        + " AND (:hairColorText = '' OR p.hairColor IN :hairColors)"
                        + " AND (:nationalityText = '' OR p.nationality IN :nationalities)",
                        Long.class)
                .setParameter("eyeColorText", eyeColor)
                .setParameter("eyeColors", eyeColors)
                .setParameter("hairColorText", hairColor)
                .setParameter("hairColors", hairColors)
                .setParameter("nationalityText", nationality)
                .setParameter("nationalities", nationalities)
                .getSingleResult();
    }

    /**
     * Сохраняет новую персону; ID выдаёт последовательность БД.
     *
     * @param person персона без ID с сохранённой локацией
     */
    public void save(Person person) {
        entityManager.persist(person);
    }

    /**
     * Отвязывает персону от работника и сразу отправляет UPDATE.
     * Ограничение уникальности worker_id не отложено, поэтому отвязка должна дойти до БД раньше
     * новой привязки.
     *
     * @param person сохранённая персона
     */
    public void unlink(Person person) {
        person.setWorker(null);
        entityManager.flush();
    }

    /**
     * Удаляет персону; её локацию не удаляет.
     *
     * @param person сохранённая персона
     */
    public void delete(Person person) {
        entityManager.remove(person);
    }

    private String filter(Map<String, List<String>> filters, String name) {
        List<String> values = filters.getOrDefault(name, List.of());
        return values.isEmpty() ? "" : values.get(0).toLowerCase(Locale.ROOT);
    }

    private <E extends Enum<E>> List<E> matching(E[] values, String filter) {
        List<E> result = new ArrayList<>();
        for (E value : values) {
            if (value.name().toLowerCase(Locale.ROOT).contains(filter)) {
                result.add(value);
            }
        }
        return result;
    }
}
