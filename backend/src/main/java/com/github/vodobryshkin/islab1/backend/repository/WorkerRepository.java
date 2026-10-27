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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Хранилище работников в БД.
 * Сортировка и фильтры по организации и персоне используют LEFT JOIN, чтобы не терять работников
 * без них.
 */
@ApplicationScoped
public class WorkerRepository {
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
     * Возвращает страницу работников, прошедших фильтры-подстроки.
     * null при сортировке идёт последним, при равных значениях порядок задаёт ID.
     *
     * @param query проверенные параметры страницы
     * @param filters параметры запроса с фильтрами контракта
     * @return работники страницы, за концом списка -- пустой список
     */
    public List<Worker> findPage(ListQuery query, Map<String, List<String>> filters) {
        long offset = (long) (query.getPage() - 1) * query.getPageSize();
        String position = filter(filters, "position");
        String status = filter(filters, "status");
        String type = filter(filters, "organization_type");
        String eyeColor = filter(filters, "person_eye_color");
        String hairColor = filter(filters, "person_hair_color");
        String nationality = filter(filters, "person_nationality");
        List<Position> positions = matching(Position.values(), position);
        List<Status> statuses = matching(Status.values(), status);
        List<OrganizationType> types = matching(OrganizationType.values(), type);
        List<Color> eyeColors = matching(Color.values(), eyeColor);
        List<Color> hairColors = matching(Color.values(), hairColor);
        List<Country> nationalities = matching(Country.values(), nationality);

        if (offset > Integer.MAX_VALUE || positions.isEmpty() || statuses.isEmpty()
                || types.isEmpty() || eyeColors.isEmpty() || hairColors.isEmpty()
                || nationalities.isEmpty()) {
            return List.of();
        }

        String order = SORT_COLUMNS.get(query.getSortBy())
                + (query.isAscending() ? " ASC" : " DESC");

        return entityManager.createQuery("SELECT w FROM Worker w"
                        + " LEFT JOIN w.organization o"
                        + " LEFT JOIN o.officialAddress oa"
                        + " LEFT JOIN o.postalAddress pa"
                        + " LEFT JOIN w.person p"
                        + " WHERE (:workerName = '' OR LOCATE(:workerName, LOWER(w.name)) > 0)"
                        + " AND (:fullName = '' OR LOCATE(:fullName, LOWER(o.fullName)) > 0)"
                        + " AND (:officialStreet = ''"
                        + " OR LOCATE(:officialStreet, LOWER(oa.street)) > 0)"
                        + " AND (:postalStreet = '' OR LOCATE(:postalStreet, LOWER(pa.street)) > 0)"
                        + " AND (:positionText = '' OR w.position IN :positions)"
                        + " AND (:statusText = '' OR w.status IN :statuses)"
                        + " AND (:typeText = '' OR o.type IN :types)"
                        + " AND (:eyeColorText = '' OR p.eyeColor IN :eyeColors)"
                        + " AND (:hairColorText = '' OR p.hairColor IN :hairColors)"
                        + " AND (:nationalityText = '' OR p.nationality IN :nationalities)"
                        + " ORDER BY " + order + " NULLS LAST, w.id ASC", Worker.class)
                .setParameter("workerName", filter(filters, "name"))
                .setParameter("fullName", filter(filters, "organization_full_name"))
                .setParameter("officialStreet",
                        filter(filters, "organization_official_address_street"))
                .setParameter("postalStreet",
                        filter(filters, "organization_postal_address_street"))
                .setParameter("positionText", position)
                .setParameter("positions", positions)
                .setParameter("statusText", status)
                .setParameter("statuses", statuses)
                .setParameter("typeText", type)
                .setParameter("types", types)
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
     * Считает работников, прошедших фильтры-подстроки.
     *
     * @param filters параметры запроса с фильтрами контракта
     * @return количество
     */
    public long count(Map<String, List<String>> filters) {
        String position = filter(filters, "position");
        String status = filter(filters, "status");
        String type = filter(filters, "organization_type");
        String eyeColor = filter(filters, "person_eye_color");
        String hairColor = filter(filters, "person_hair_color");
        String nationality = filter(filters, "person_nationality");
        List<Position> positions = matching(Position.values(), position);
        List<Status> statuses = matching(Status.values(), status);
        List<OrganizationType> types = matching(OrganizationType.values(), type);
        List<Color> eyeColors = matching(Color.values(), eyeColor);
        List<Color> hairColors = matching(Color.values(), hairColor);
        List<Country> nationalities = matching(Country.values(), nationality);

        if (positions.isEmpty() || statuses.isEmpty() || types.isEmpty()
                || eyeColors.isEmpty() || hairColors.isEmpty() || nationalities.isEmpty()) {
            return 0;
        }

        return entityManager.createQuery("SELECT COUNT(w) FROM Worker w"
                        + " LEFT JOIN w.organization o"
                        + " LEFT JOIN o.officialAddress oa"
                        + " LEFT JOIN o.postalAddress pa"
                        + " LEFT JOIN w.person p"
                        + " WHERE (:workerName = '' OR LOCATE(:workerName, LOWER(w.name)) > 0)"
                        + " AND (:fullName = '' OR LOCATE(:fullName, LOWER(o.fullName)) > 0)"
                        + " AND (:officialStreet = ''"
                        + " OR LOCATE(:officialStreet, LOWER(oa.street)) > 0)"
                        + " AND (:postalStreet = '' OR LOCATE(:postalStreet, LOWER(pa.street)) > 0)"
                        + " AND (:positionText = '' OR w.position IN :positions)"
                        + " AND (:statusText = '' OR w.status IN :statuses)"
                        + " AND (:typeText = '' OR o.type IN :types)"
                        + " AND (:eyeColorText = '' OR p.eyeColor IN :eyeColors)"
                        + " AND (:hairColorText = '' OR p.hairColor IN :hairColors)"
                        + " AND (:nationalityText = '' OR p.nationality IN :nationalities)",
                        Long.class)
                .setParameter("workerName", filter(filters, "name"))
                .setParameter("fullName", filter(filters, "organization_full_name"))
                .setParameter("officialStreet",
                        filter(filters, "organization_official_address_street"))
                .setParameter("postalStreet",
                        filter(filters, "organization_postal_address_street"))
                .setParameter("positionText", position)
                .setParameter("positions", positions)
                .setParameter("statusText", status)
                .setParameter("statuses", statuses)
                .setParameter("typeText", type)
                .setParameter("types", types)
                .setParameter("eyeColorText", eyeColor)
                .setParameter("eyeColors", eyeColors)
                .setParameter("hairColorText", hairColor)
                .setParameter("hairColors", hairColors)
                .setParameter("nationalityText", nationality)
                .setParameter("nationalities", nationalities)
                .getSingleResult();
    }

    /**
     * Считает работников с указанным статусом.
     *
     * @param status статус, null -- работники без статуса
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
     * @return количество, работники без статуса не учитываются
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
     * @return работники с вложенными объектами, без совпадений -- пустой список
     */
    public List<Worker> findByRatingLessThan(int rating) {
        return entityManager.createQuery(
                        "SELECT w FROM Worker w WHERE w.rating < :rating ORDER BY w.id",
                        Worker.class)
                .setParameter("rating", rating)
                .getResultList();
    }

    /**
     * Возвращает работников организации, блокируя их строки до конца транзакции.
     * Порядок по ID одинаков для всех транзакций и исключает взаимную блокировку.
     *
     * @param organizationId ID организации
     * @return работники организации, без работников -- пустой список
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
     * @return true, если с работником связана персона
     */
    public boolean isReferencedByPerson(int id) {
        return entityManager.createQuery(
                        "SELECT COUNT(p) FROM Person p WHERE p.worker.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult() > 0;
    }

    /**
     * Сохраняет нового работника и сразу отправляет INSERT, ID выдаёт последовательность БД.
     *
     * @param worker работник без ID с сохранёнными связанными объектами
     */
    public void save(Worker worker) {
        entityManager.persist(worker);
        entityManager.flush();
    }

    /**
     * Удаляет работника и сразу отправляет DELETE, связанные объекты не удаляет.
     *
     * @param worker сохранённый работник
     */
    public void delete(Worker worker) {
        entityManager.remove(worker);
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
