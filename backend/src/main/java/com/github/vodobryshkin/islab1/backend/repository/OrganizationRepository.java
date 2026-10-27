package com.github.vodobryshkin.islab1.backend.repository;

import com.github.vodobryshkin.islab1.backend.domain.Organization;
import com.github.vodobryshkin.islab1.backend.domain.OrganizationType;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Хранилище организаций в БД. Транзакциями управляет вызывающий сервис.
 */
@ApplicationScoped
public class OrganizationRepository {
    private static final Map<String, String> SORT_COLUMNS = Map.of(
            "id", "o.id",
            "full_name", "LOWER(o.fullName)",
            "annual_turnover", "o.annualTurnover",
            "employees_count", "o.employeesCount",
            "rating", "o.rating",
            "type", "o.type",
            "official_address_id", "o.officialAddress.id",
            "postal_address_id", "o.postalAddress.id",
            "official_address_street", "LOWER(o.officialAddress.street)",
            "postal_address_street", "LOWER(o.postalAddress.street)");

    /**
     * Текстовые фильтры: подстрока без учёта регистра. LOCATE ищет подстроку
     * буквально, поэтому % и _ не требуют экранирования.
     */
    private static final Map<String, String> TEXT_FILTERS = Map.of(
            "full_name", "o.fullName",
            "official_address_street", "o.officialAddress.street",
            "postal_address_street", "o.postalAddress.street");
    private static final String TYPE = "type";

    @PersistenceContext(unitName = "is-lab1")
    private EntityManager entityManager;

    /**
     * Ищет организацию по ID.
     *
     * @param id ID организации
     * @return организация или пустой результат
     */
    public Optional<Organization> findById(long id) {
        return Optional.ofNullable(entityManager.find(Organization.class, id));
    }

    /**
     * Ищет организацию по полному названию с учётом регистра, как уникальный индекс БД.
     *
     * @param fullName полное название
     * @return организация или пустой результат
     */
    public Optional<Organization> findByFullName(String fullName) {
        return entityManager.createQuery(
                        "SELECT o FROM Organization o WHERE o.fullName = :fullName",
                        Organization.class)
                .setParameter("fullName", fullName)
                .getResultStream()
                .findFirst();
    }

    /**
     * Возвращает страницу организаций. null при сортировке идёт последним,
     * при равных значениях порядок задаёт ID по возрастанию.
     *
     * @param query проверенные параметры страницы
     * @param filters параметры запроса; учитываются full_name, type,
     *     official_address_street, postal_address_street
     * @return организации страницы; за концом списка — пустой список
     */
    public List<Organization> findPage(ListQuery query, Map<String, List<String>> filters) {
        Map<String, Object> parameters = parameters(filters);
        long offset = (long) (query.getPage() - 1) * query.getPageSize();
        if (offset > Integer.MAX_VALUE || List.of().equals(parameters.get(TYPE))) {
            return List.of();
        }
        String order = SORT_COLUMNS.get(query.getSortBy())
                + (query.isAscending() ? " ASC" : " DESC");
        return bind(entityManager.createQuery("SELECT o FROM Organization o" + where(parameters)
                        + " ORDER BY " + order + " NULLS LAST, o.id ASC", Organization.class),
                parameters)
                .setFirstResult((int) offset)
                .setMaxResults(query.getPageSize())
                .getResultList();
    }

    /**
     * Считает организации с учётом фильтров.
     *
     * @param filters параметры запроса, как в findPage
     * @return количество
     */
    public long count(Map<String, List<String>> filters) {
        Map<String, Object> parameters = parameters(filters);
        if (List.of().equals(parameters.get(TYPE))) {
            return 0;
        }
        return bind(entityManager.createQuery(
                "SELECT COUNT(o) FROM Organization o" + where(parameters), Long.class), parameters)
                .getSingleResult();
    }

    /**
     * Проверяет, ссылается ли на организацию хотя бы один работник.
     *
     * @param id ID организации
     * @return true, если организация используется
     */
    public boolean isUsed(long id) {
        return entityManager.createQuery(
                        "SELECT COUNT(w) FROM Worker w WHERE w.organization.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult() > 0;
    }

    /**
     * Сохраняет новую организацию и сразу отправляет INSERT, чтобы нарушение уникальности
     * full_name возникло внутри транзакции сервиса.
     *
     * @param organization организация без ID с сохранёнными адресами
     */
    public void save(Organization organization) {
        entityManager.persist(organization);
        entityManager.flush();
    }

    /**
     * Удаляет организацию и сразу отправляет DELETE; её адреса не удаляет.
     *
     * @param organization сохранённая организация
     */
    public void delete(Organization organization) {
        entityManager.remove(organization);
        entityManager.flush();
    }

    /**
     * Значения непустых фильтров: строка в нижнем регистре для текстовых и список
     * подходящих констант для type. Пустой список означает, что ничего не подходит.
     */
    private Map<String, Object> parameters(Map<String, List<String>> filters) {
        return Stream.concat(TEXT_FILTERS.keySet().stream(), Stream.of(TYPE))
                .filter(name -> !filter(filters, name).isEmpty())
                .collect(Collectors.toMap(name -> name, name -> TYPE.equals(name)
                        ? Stream.of(OrganizationType.values())
                                .filter(type -> type.name().toLowerCase(Locale.ROOT)
                                        .contains(filter(filters, name)))
                                .toList()
                        : filter(filters, name)));
    }

    private String filter(Map<String, List<String>> filters, String name) {
        List<String> values = filters.getOrDefault(name, List.of());
        return values.isEmpty() ? "" : values.get(0).toLowerCase(Locale.ROOT);
    }

    private String where(Map<String, Object> parameters) {
        return parameters.isEmpty() ? "" : " WHERE " + parameters.keySet().stream()
                .map(name -> TYPE.equals(name)
                        ? "o.type IN :type"
                        : "LOCATE(:" + name + ", LOWER(" + TEXT_FILTERS.get(name) + ")) > 0")
                .collect(Collectors.joining(" AND "));
    }

    private <T> TypedQuery<T> bind(TypedQuery<T> query, Map<String, Object> parameters) {
        parameters.forEach(query::setParameter);
        return query;
    }
}
