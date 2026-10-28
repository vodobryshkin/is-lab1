package com.github.vodobryshkin.islab1.backend.repository;

import com.github.vodobryshkin.islab1.backend.domain.Organization;
import com.github.vodobryshkin.islab1.backend.domain.OrganizationType;
import com.github.vodobryshkin.islab1.backend.dto.common.ListQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Хранилище организаций.
 */
@ApplicationScoped
public class OrganizationRepository {
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
     * Ищет организацию по полному названию.
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
     * Возвращает страницу организаций с фильтрами по подстроке.
     *
     * @param query параметры страницы
     * @param fullName подстрока полного названия или null
     * @param type подстрока типа или null
     * @return организации страницы
     */
    public List<Organization> findPage(ListQuery query, String fullName, String type) {
        String fullNameText = fullName == null ? "" : fullName.toLowerCase(Locale.ROOT);
        String typeText = type == null ? "" : type.toLowerCase(Locale.ROOT);
        List<OrganizationType> types = Stream.of(OrganizationType.values())
                .filter(value -> value.name().toLowerCase(Locale.ROOT).contains(typeText))
                .toList();

        if (types.isEmpty()) {
            return List.of();
        }

        long offset = (long) (query.getPage() - 1) * query.getPageSize();
        String column = switch (query.getSortBy()) {
            case "full_name" -> "LOWER(o.fullName)";
            case "annual_turnover" -> "o.annualTurnover";
            case "employees_count" -> "o.employeesCount";
            case "rating" -> "o.rating";
            case "type" -> "o.type";
            default -> "o.id";
        };
        String order = query.isAscending() ? "ASC" : "DESC";

        return entityManager.createQuery("SELECT o FROM Organization o"
                        + " WHERE LOCATE(:fullName, LOWER(o.fullName)) > 0"
                        + " AND (:type = '' OR o.type IN :types)"
                        + " ORDER BY " + column + " " + order + " NULLS LAST, o.id",
                        Organization.class)
                .setParameter("fullName", fullNameText)
                .setParameter("type", typeText)
                .setParameter("types", types)
                .setFirstResult((int) Math.min(offset, Integer.MAX_VALUE))
                .setMaxResults(query.getPageSize())
                .getResultList();
    }

    /**
     * Считает организации с фильтрами по подстроке.
     *
     * @param fullName подстрока полного названия или null
     * @param type подстрока типа или null
     * @return количество
     */
    public long count(String fullName, String type) {
        String fullNameText = fullName == null ? "" : fullName.toLowerCase(Locale.ROOT);
        String typeText = type == null ? "" : type.toLowerCase(Locale.ROOT);
        List<OrganizationType> types = Stream.of(OrganizationType.values())
                .filter(value -> value.name().toLowerCase(Locale.ROOT).contains(typeText))
                .toList();

        if (types.isEmpty()) {
            return 0;
        }

        return entityManager.createQuery("SELECT COUNT(o) FROM Organization o"
                        + " WHERE LOCATE(:fullName, LOWER(o.fullName)) > 0"
                        + " AND (:type = '' OR o.type IN :types)", Long.class)
                .setParameter("fullName", fullNameText)
                .setParameter("type", typeText)
                .setParameter("types", types)
                .getSingleResult();
    }

    /**
     * Проверяет, есть ли у организации работники.
     *
     * @param id ID организации
     * @return true, если работники есть
     */
    public boolean isUsed(long id) {
        return entityManager.createQuery(
                        "SELECT COUNT(w) FROM Worker w WHERE w.organization.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult() > 0;
    }

    /**
     * Сохраняет новую организацию.
     *
     * @param organization организация
     */
    public void save(Organization organization) {
        entityManager.persist(organization);
        entityManager.flush();
    }

    /**
     * Удаляет организацию.
     *
     * @param organization организация
     */
    public void delete(Organization organization) {
        entityManager.remove(organization);
        entityManager.flush();
    }
}
