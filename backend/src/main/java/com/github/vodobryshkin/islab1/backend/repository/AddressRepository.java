package com.github.vodobryshkin.islab1.backend.repository;

import com.github.vodobryshkin.islab1.backend.domain.Address;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Хранилище адресов.
 */
@ApplicationScoped
public class AddressRepository {
    @PersistenceContext(unitName = "is-lab1")
    private EntityManager entityManager;

    /**
     * Ищет адрес по ID.
     *
     * @param id ID адреса
     * @return адрес или пустой результат
     */
    public Optional<Address> findById(long id) {
        return Optional.ofNullable(entityManager.find(Address.class, id));
    }

    /**
     * Возвращает страницу адресов с фильтром по подстроке улицы.
     *
     * @param query параметры страницы
     * @param street подстрока улицы или null
     * @return адреса страницы
     */
    public List<Address> findPage(ListQuery query, String street) {
        long offset = (long) (query.getPage() - 1) * query.getPageSize();
        String column = "street".equals(query.getSortBy()) ? "LOWER(a.street)" : "a.id";
        String order = query.isAscending() ? "ASC" : "DESC";

        return entityManager.createQuery("SELECT a FROM Address a"
                        + " WHERE LOCATE(:street, LOWER(a.street)) > 0"
                        + " ORDER BY " + column + " " + order + ", a.id", Address.class)
                .setParameter("street", street == null ? "" : street.toLowerCase(Locale.ROOT))
                .setFirstResult((int) Math.min(offset, Integer.MAX_VALUE))
                .setMaxResults(query.getPageSize())
                .getResultList();
    }

    /**
     * Считает адреса с фильтром по подстроке улицы.
     *
     * @param street подстрока улицы или null
     * @return количество
     */
    public long count(String street) {
        return entityManager.createQuery("SELECT COUNT(a) FROM Address a"
                        + " WHERE LOCATE(:street, LOWER(a.street)) > 0",
                        Long.class)
                .setParameter("street", street == null ? "" : street.toLowerCase(Locale.ROOT))
                .getSingleResult();
    }

    /**
     * Проверяет, используется ли адрес организацией.
     *
     * @param id ID адреса
     * @return true, если адрес используется
     */
    public boolean isUsed(long id) {
        return entityManager.createQuery("SELECT COUNT(o) FROM Organization o WHERE "
                        + "o.officialAddress.id = :id OR o.postalAddress.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult() > 0;
    }

    /**
     * Сохраняет новый адрес.
     *
     * @param address адрес
     */
    public void save(Address address) {
        entityManager.persist(address);
    }

    /**
     * Удаляет адрес.
     *
     * @param address адрес
     */
    public void delete(Address address) {
        entityManager.remove(address);
        entityManager.flush();
    }
}
