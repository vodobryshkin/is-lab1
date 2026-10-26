package com.github.vodobryshkin.islab1.backend.repository;

import com.github.vodobryshkin.islab1.backend.domain.Address;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Хранилище адресов в БД. Транзакциями управляет вызывающий сервис.
 */
@ApplicationScoped
public class AddressRepository {
    /** Поля сортировки контракта; строки сравниваются без учёта регистра. */
    private static final Map<String, String> SORT_COLUMNS =
            Map.of("id", "a.id", "street", "LOWER(a.street)");

    /** Подстрока без учёта регистра; {@code %} и {@code _} в фильтре — обычные символы. */
    private static final String STREET_FILTER = " WHERE LOWER(a.street) LIKE :street ESCAPE '\\'";

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
     * Возвращает страницу адресов; при равных значениях порядок задаёт ID по возрастанию.
     *
     * @param query проверенные параметры страницы
     * @param street подстрока улицы; {@code null} или пустая строка не фильтрует
     * @return адреса страницы; за концом списка — пустой список
     */
    public List<Address> findPage(ListQuery query, String street) {
        long offset = (long) (query.page() - 1) * query.pageSize();
        if (offset > Integer.MAX_VALUE) {
            return List.of();
        }
        String order = SORT_COLUMNS.get(query.sortBy()) + (query.ascending() ? " ASC" : " DESC");
        return filtered(entityManager.createQuery("SELECT a FROM Address a" + where(street)
                        + " ORDER BY " + order + ", a.id ASC", Address.class), street)
                .setFirstResult((int) offset)
                .setMaxResults(query.pageSize())
                .getResultList();
    }

    /**
     * Считает адреса с учётом фильтра.
     *
     * @param street подстрока улицы; {@code null} или пустая строка не фильтрует
     * @return количество
     */
    public long count(String street) {
        return filtered(entityManager.createQuery(
                "SELECT COUNT(a) FROM Address a" + where(street), Long.class), street)
                .getSingleResult();
    }

    /**
     * Проверяет, используется ли адрес организацией как официальный или почтовый.
     *
     * @param id ID адреса
     * @return {@code true}, если адрес используется
     */
    public boolean isUsed(long id) {
        return entityManager.createQuery("SELECT COUNT(o) FROM Organization o WHERE "
                        + "o.officialAddress.id = :id OR o.postalAddress.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult() > 0;
    }

    /**
     * Сохраняет новый адрес; ID выдаёт последовательность БД.
     *
     * @param address адрес без ID с сохранённой локацией или без неё
     */
    public void save(Address address) {
        entityManager.persist(address);
    }

    /**
     * Удаляет адрес и сразу отправляет DELETE; локацию адреса не удаляет.
     *
     * @param address сохранённый адрес
     */
    public void delete(Address address) {
        entityManager.remove(address);
        entityManager.flush();
    }

    private String where(String street) {
        return isBlank(street) ? "" : STREET_FILTER;
    }

    private <T> TypedQuery<T> filtered(TypedQuery<T> query, String street) {
        if (isBlank(street)) {
            return query;
        }
        String escaped = street.toLowerCase(Locale.ROOT).replace("\\", "\\\\")
                .replace("%", "\\%").replace("_", "\\_");
        return query.setParameter("street", "%" + escaped + "%");
    }

    /** Пустой фильтр не фильтрует, как требует контракт. */
    private boolean isBlank(String street) {
        return street == null || street.isEmpty();
    }
}
