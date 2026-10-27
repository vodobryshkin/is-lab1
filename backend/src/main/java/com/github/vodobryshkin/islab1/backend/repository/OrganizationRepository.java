package com.github.vodobryshkin.islab1.backend.repository;

import com.github.vodobryshkin.islab1.backend.domain.Organization;
import com.github.vodobryshkin.islab1.backend.domain.OrganizationType;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

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
     * Возвращает страницу организаций, прошедших фильтры-подстроки.
     * null при сортировке идёт последним, при равных значениях порядок задаёт ID.
     *
     * @param query проверенные параметры страницы
     * @param filters параметры запроса с фильтрами контракта
     * @return организации страницы; за концом списка -- пустой список
     */
    public List<Organization> findPage(ListQuery query, Map<String, List<String>> filters) {
        long offset = (long) (query.getPage() - 1) * query.getPageSize();
        String type = filter(filters, "type");
        List<OrganizationType> types = matching(OrganizationType.values(), type);
        if (offset > Integer.MAX_VALUE || types.isEmpty()) {
            return List.of();
        }
        String order = SORT_COLUMNS.get(query.getSortBy())
                + (query.isAscending() ? " ASC" : " DESC");
        return entityManager.createQuery("SELECT o FROM Organization o"
                        + " WHERE (:fullName = '' OR LOCATE(:fullName, LOWER(o.fullName)) > 0)"
                        + " AND (:officialStreet = ''"
                        + " OR LOCATE(:officialStreet, LOWER(o.officialAddress.street)) > 0)"
                        + " AND (:postalStreet = ''"
                        + " OR LOCATE(:postalStreet, LOWER(o.postalAddress.street)) > 0)"
                        + " AND (:typeText = '' OR o.type IN :types)"
                        + " ORDER BY " + order + " NULLS LAST, o.id ASC", Organization.class)
                .setParameter("fullName", filter(filters, "full_name"))
                .setParameter("officialStreet", filter(filters, "official_address_street"))
                .setParameter("postalStreet", filter(filters, "postal_address_street"))
                .setParameter("typeText", type)
                .setParameter("types", types)
                .setFirstResult((int) offset)
                .setMaxResults(query.getPageSize())
                .getResultList();
    }

    /**
     * Считает организации, прошедшие фильтры-подстроки.
     *
     * @param filters параметры запроса с фильтрами контракта
     * @return количество
     */
    public long count(Map<String, List<String>> filters) {
        String type = filter(filters, "type");
        List<OrganizationType> types = matching(OrganizationType.values(), type);
        if (types.isEmpty()) {
            return 0;
        }
        return entityManager.createQuery("SELECT COUNT(o) FROM Organization o"
                        + " WHERE (:fullName = '' OR LOCATE(:fullName, LOWER(o.fullName)) > 0)"
                        + " AND (:officialStreet = ''"
                        + " OR LOCATE(:officialStreet, LOWER(o.officialAddress.street)) > 0)"
                        + " AND (:postalStreet = ''"
                        + " OR LOCATE(:postalStreet, LOWER(o.postalAddress.street)) > 0)"
                        + " AND (:typeText = '' OR o.type IN :types)", Long.class)
                .setParameter("fullName", filter(filters, "full_name"))
                .setParameter("officialStreet", filter(filters, "official_address_street"))
                .setParameter("postalStreet", filter(filters, "postal_address_street"))
                .setParameter("typeText", type)
                .setParameter("types", types)
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
