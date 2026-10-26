package com.github.vodobryshkin.islab1.backend.repository;

import com.github.vodobryshkin.islab1.backend.domain.Color;
import com.github.vodobryshkin.islab1.backend.domain.Country;
import com.github.vodobryshkin.islab1.backend.domain.Person;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

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

    /** Фильтры контракта: колонка и все значения перечисления. */
    private static final Map<String, String> FILTER_COLUMNS = Map.of(
            "eye_color", "p.eyeColor",
            "hair_color", "p.hairColor",
            "nationality", "p.nationality");
    private static final Map<String, List<Enum<?>>> FILTER_VALUES = Map.of(
            "eye_color", List.of(Color.values()),
            "hair_color", List.of(Color.values()),
            "nationality", List.of(Country.values()));

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
     * Возвращает страницу персон. {@code null} при сортировке идёт последним,
     * при равных значениях порядок задаёт ID по возрастанию.
     *
     * @param query проверенные параметры страницы
     * @param filters параметры запроса; учитываются {@code eye_color}, {@code hair_color},
     *     {@code nationality}
     * @return персоны страницы; за концом списка — пустой список
     */
    public List<Person> findPage(ListQuery query, Map<String, List<String>> filters) {
        Map<String, List<Enum<?>>> matches = matches(filters);
        long offset = (long) (query.page() - 1) * query.pageSize();
        if (offset > Integer.MAX_VALUE || matches.containsValue(List.of())) {
            return List.of();
        }
        String order = SORT_COLUMNS.get(query.sortBy()) + (query.ascending() ? " ASC" : " DESC");
        return bind(entityManager.createQuery("SELECT p FROM Person p" + where(matches)
                        + " ORDER BY " + order + " NULLS LAST, p.id ASC", Person.class), matches)
                .setFirstResult((int) offset)
                .setMaxResults(query.pageSize())
                .getResultList();
    }

    /**
     * Считает персоны с учётом фильтров.
     *
     * @param filters параметры запроса; учитываются {@code eye_color}, {@code hair_color},
     *     {@code nationality}
     * @return количество
     */
    public long count(Map<String, List<String>> filters) {
        Map<String, List<Enum<?>>> matches = matches(filters);
        if (matches.containsValue(List.of())) {
            return 0;
        }
        return bind(entityManager.createQuery(
                "SELECT COUNT(p) FROM Person p" + where(matches), Long.class), matches)
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
     * Удаляет персону; её локацию не удаляет.
     *
     * @param person сохранённая персона
     */
    public void delete(Person person) {
        entityManager.remove(person);
    }

    /**
     * Переводит непустые фильтры-подстроки в значения перечислений, чьи имена их содержат
     * без учёта регистра. Пустой список значений означает, что ничего не подходит.
     */
    private Map<String, List<Enum<?>>> matches(Map<String, List<String>> filters) {
        return FILTER_COLUMNS.keySet().stream()
                .filter(name -> !filter(filters, name).isEmpty())
                .collect(Collectors.toMap(name -> name, name -> FILTER_VALUES.get(name).stream()
                        .filter(value -> value.name().toLowerCase(Locale.ROOT)
                                .contains(filter(filters, name)))
                        .toList(), (first, second) -> first, TreeMap::new));
    }

    private String filter(Map<String, List<String>> filters, String name) {
        List<String> values = filters.getOrDefault(name, List.of());
        return values.isEmpty() ? "" : values.get(0).toLowerCase(Locale.ROOT);
    }

    private String where(Map<String, List<Enum<?>>> matches) {
        return matches.isEmpty() ? "" : " WHERE " + matches.keySet().stream()
                .map(key -> FILTER_COLUMNS.get(key) + " IN :" + key)
                .collect(Collectors.joining(" AND "));
    }

    private <T> TypedQuery<T> bind(TypedQuery<T> query, Map<String, List<Enum<?>>> matches) {
        matches.forEach(query::setParameter);
        return query;
    }
}
