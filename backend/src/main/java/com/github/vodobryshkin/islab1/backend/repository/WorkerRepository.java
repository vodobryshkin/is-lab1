package com.github.vodobryshkin.islab1.backend.repository;

import com.github.vodobryshkin.islab1.backend.domain.Color;
import com.github.vodobryshkin.islab1.backend.domain.Country;
import com.github.vodobryshkin.islab1.backend.domain.OrganizationType;
import com.github.vodobryshkin.islab1.backend.domain.Position;
import com.github.vodobryshkin.islab1.backend.domain.Status;
import com.github.vodobryshkin.islab1.backend.domain.Worker;
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
import java.util.stream.Stream;

/**
 * Хранилище работников в БД. Транзакциями управляет вызывающий сервис.
 *
 * <p>Организация и персона работника могут отсутствовать, поэтому сортировка и фильтры
 * по их полям используют явные {@code LEFT JOIN}: путь {@code w.organization.fullName}
 * дал бы внутреннее соединение и исключил бы работников без организации.
 */
@ApplicationScoped
public class WorkerRepository {
    private static final String FROM = " FROM Worker w LEFT JOIN w.organization o"
            + " LEFT JOIN o.officialAddress oa LEFT JOIN o.postalAddress pa"
            + " LEFT JOIN w.person p";

    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
            Map.entry("id", "w.id"),
            Map.entry("name", "LOWER(w.name)"),
            Map.entry("salary", "w.salary"),
            Map.entry("rating", "w.rating"),
            Map.entry("start_date", "w.startDate"),
            Map.entry("creation_date", "w.creationDate"),
            Map.entry("position", "w.position"),
            Map.entry("status", "w.status"),
            Map.entry("organization_full_name", "LOWER(o.fullName)"),
            Map.entry("organization_type", "o.type"),
            Map.entry("organization_official_address_street", "LOWER(oa.street)"),
            Map.entry("organization_postal_address_street", "LOWER(pa.street)"),
            Map.entry("person_eye_color", "p.eyeColor"),
            Map.entry("person_hair_color", "p.hairColor"),
            Map.entry("person_nationality", "p.nationality"));

    /**
     * Текстовые фильтры: подстрока без учёта регистра. {@code LOCATE} ищет подстроку
     * буквально, а {@code null} в колонке не проходит фильтр.
     */
    private static final Map<String, String> TEXT_FILTERS = Map.of(
            "name", "w.name",
            "organization_full_name", "o.fullName",
            "organization_official_address_street", "oa.street",
            "organization_postal_address_street", "pa.street");

    /** Фильтры по перечислениям: колонка и все значения. */
    private static final Map<String, String> ENUM_COLUMNS = Map.of(
            "position", "w.position",
            "status", "w.status",
            "organization_type", "o.type",
            "person_eye_color", "p.eyeColor",
            "person_hair_color", "p.hairColor",
            "person_nationality", "p.nationality");
    private static final Map<String, List<Enum<?>>> ENUM_VALUES = Map.of(
            "position", List.of(Position.values()),
            "status", List.of(Status.values()),
            "organization_type", List.of(OrganizationType.values()),
            "person_eye_color", List.of(Color.values()),
            "person_hair_color", List.of(Color.values()),
            "person_nationality", List.of(Country.values()));

    @PersistenceContext(unitName = "is-lab1")
    private EntityManager entityManager;

    /**
     * Ищет работника по ID.
     *
     * @param id ID работника
     * @return работник с вложенными объектами или пустой результат
     */
    public Optional<Worker> findById(int id) {
        return Optional.ofNullable(entityManager.find(Worker.class, id));
    }

    /**
     * Возвращает страницу работников. {@code null}, в том числе отсутствующая организация
     * или персона, при сортировке идёт последним; при равных значениях — ID по возрастанию.
     *
     * @param query проверенные параметры страницы
     * @param filters параметры запроса; учитываются фильтры контракта, соединённые через AND
     * @return работники страницы; за концом списка — пустой список
     */
    public List<Worker> findPage(ListQuery query, Map<String, List<String>> filters) {
        Map<String, Object> parameters = parameters(filters);
        long offset = (long) (query.page() - 1) * query.pageSize();
        if (offset > Integer.MAX_VALUE || parameters.containsValue(List.of())) {
            return List.of();
        }
        String order = SORT_COLUMNS.get(query.sortBy()) + (query.ascending() ? " ASC" : " DESC");
        return bind(entityManager.createQuery("SELECT w" + FROM + where(parameters)
                        + " ORDER BY " + order + " NULLS LAST, w.id ASC", Worker.class),
                parameters)
                .setFirstResult((int) offset)
                .setMaxResults(query.pageSize())
                .getResultList();
    }

    /**
     * Считает работников с учётом фильтров.
     *
     * @param filters параметры запроса, как в {@link #findPage}
     * @return количество
     */
    public long count(Map<String, List<String>> filters) {
        Map<String, Object> parameters = parameters(filters);
        if (parameters.containsValue(List.of())) {
            return 0;
        }
        return bind(entityManager.createQuery("SELECT COUNT(w)" + FROM + where(parameters),
                Long.class), parameters).getSingleResult();
    }

    /**
     * Считает работников с указанным статусом.
     *
     * @param status статус; {@code null} — работники без статуса
     * @return количество
     */
    public long countByStatus(Status status) {
        if (status == null) {
            return entityManager.createQuery(
                            "SELECT COUNT(w) FROM Worker w WHERE w.status IS NULL", Long.class)
                    .getSingleResult();
        }
        return entityManager.createQuery(
                        "SELECT COUNT(w) FROM Worker w WHERE w.status = :value", Long.class)
                .setParameter("value", status)
                .getSingleResult();
    }

    /**
     * Считает работников, чей статус входит в список.
     *
     * @param statuses непустой список статусов
     * @return количество; работники без статуса не учитываются
     */
    public long countByStatusIn(List<Status> statuses) {
        return entityManager.createQuery(
                        "SELECT COUNT(w) FROM Worker w WHERE w.status IN :statuses", Long.class)
                .setParameter("statuses", statuses)
                .getSingleResult();
    }

    /**
     * Возвращает всех работников с рейтингом строго меньше порога, по возрастанию ID.
     *
     * @param rating порог
     * @return работники с вложенными объектами; без совпадений — пустой список
     */
    public List<Worker> findByRatingLessThan(int rating) {
        return entityManager.createQuery(
                        "SELECT w FROM Worker w WHERE w.rating < :rating ORDER BY w.id",
                        Worker.class)
                .setParameter("rating", rating)
                .getResultList();
    }

    /**
     * Возвращает работников организации и блокирует их строки до конца транзакции,
     * чтобы одновременное изменение не потерялось. Порядок по ID одинаков для всех
     * транзакций и не даёт им заблокировать друг друга по кругу.
     *
     * @param organizationId ID организации
     * @return работники организации; без работников — пустой список
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
     * Проверяет, ссылается ли на работника персона.
     *
     * @param id ID работника
     * @return {@code true}, если с работником связана персона
     */
    public boolean isReferencedByPerson(int id) {
        return entityManager.createQuery(
                        "SELECT COUNT(p) FROM Person p WHERE p.worker.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult() > 0;
    }

    /**
     * Сохраняет нового работника и сразу отправляет INSERT; ID выдаёт последовательность БД.
     *
     * @param worker работник без ID с сохранёнными связанными объектами
     */
    public void save(Worker worker) {
        entityManager.persist(worker);
        entityManager.flush();
    }

    /**
     * Удаляет работника и сразу отправляет DELETE; связанные объекты не удаляет.
     *
     * @param worker сохранённый работник
     */
    public void delete(Worker worker) {
        entityManager.remove(worker);
        entityManager.flush();
    }

    /**
     * Значения непустых фильтров: строка в нижнем регистре для текстовых и список
     * подходящих констант для перечислений. Пустой список означает, что ничего не подходит.
     */
    private Map<String, Object> parameters(Map<String, List<String>> filters) {
        return Stream.concat(TEXT_FILTERS.keySet().stream(), ENUM_COLUMNS.keySet().stream())
                .filter(name -> !filter(filters, name).isEmpty())
                .collect(Collectors.toMap(name -> name, name -> ENUM_COLUMNS.containsKey(name)
                        ? ENUM_VALUES.get(name).stream()
                                .filter(value -> value.name().toLowerCase(Locale.ROOT)
                                        .contains(filter(filters, name)))
                                .toList()
                        : filter(filters, name), (first, second) -> first, TreeMap::new));
    }

    private String filter(Map<String, List<String>> filters, String name) {
        List<String> values = filters.getOrDefault(name, List.of());
        return values.isEmpty() ? "" : values.get(0).toLowerCase(Locale.ROOT);
    }

    private String where(Map<String, Object> parameters) {
        return parameters.isEmpty() ? "" : " WHERE " + parameters.keySet().stream()
                .map(name -> ENUM_COLUMNS.containsKey(name)
                        ? ENUM_COLUMNS.get(name) + " IN :" + name
                        : "LOCATE(:" + name + ", LOWER(" + TEXT_FILTERS.get(name) + ")) > 0")
                .collect(Collectors.joining(" AND "));
    }

    private <T> TypedQuery<T> bind(TypedQuery<T> query, Map<String, Object> parameters) {
        parameters.forEach(query::setParameter);
        return query;
    }
}
