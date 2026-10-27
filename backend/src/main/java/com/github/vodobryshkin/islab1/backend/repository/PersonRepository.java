package com.github.vodobryshkin.islab1.backend.repository;

import com.github.vodobryshkin.islab1.backend.domain.Color;
import com.github.vodobryshkin.islab1.backend.domain.Country;
import com.github.vodobryshkin.islab1.backend.domain.Person;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Хранилище персон.
 */
@ApplicationScoped
public class PersonRepository {
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
     * Ищет персону по ID и блокирует её до конца транзакции.
     *
     * @param id ID персоны
     * @return персона или пустой результат
     */
    public Optional<Person> findByIdForUpdate(long id) {
        return Optional.ofNullable(
                entityManager.find(Person.class, id, LockModeType.PESSIMISTIC_WRITE));
    }

    /**
     * Возвращает страницу персон с фильтрами по подстроке.
     *
     * @param query параметры страницы
     * @param eyeColor подстрока цвета глаз или null
     * @param hairColor подстрока цвета волос или null
     * @param nationality подстрока национальности или null
     * @return персоны страницы
     */
    public List<Person> findPage(ListQuery query, String eyeColor, String hairColor,
            String nationality) {
        String eyeColorText = eyeColor == null ? "" : eyeColor.toLowerCase(Locale.ROOT);
        String hairColorText = hairColor == null ? "" : hairColor.toLowerCase(Locale.ROOT);
        String nationalityText = nationality == null ? "" : nationality.toLowerCase(Locale.ROOT);
        List<Color> eyeColors = Stream.of(Color.values())
                .filter(value -> value.name().toLowerCase(Locale.ROOT).contains(eyeColorText))
                .toList();
        List<Color> hairColors = Stream.of(Color.values())
                .filter(value -> value.name().toLowerCase(Locale.ROOT).contains(hairColorText))
                .toList();
        List<Country> nationalities = Stream.of(Country.values())
                .filter(value -> value.name().toLowerCase(Locale.ROOT).contains(nationalityText))
                .toList();

        if (eyeColors.isEmpty() || hairColors.isEmpty() || nationalities.isEmpty()) {
            return List.of();
        }

        long offset = (long) (query.getPage() - 1) * query.getPageSize();
        String column = switch (query.getSortBy()) {
            case "eye_color" -> "p.eyeColor";
            case "hair_color" -> "p.hairColor";
            case "nationality" -> "p.nationality";
            case "birthday" -> "p.birthday";
            case "weight" -> "p.weight";
            default -> "p.id";
        };
        String order = query.isAscending() ? "ASC" : "DESC";

        return entityManager.createQuery("SELECT p FROM Person p"
                        + " WHERE (:eyeColor = '' OR p.eyeColor IN :eyeColors)"
                        + " AND (:hairColor = '' OR p.hairColor IN :hairColors)"
                        + " AND (:nationality = '' OR p.nationality IN :nationalities)"
                        + " ORDER BY " + column + " " + order + " NULLS LAST, p.id",
                        Person.class)
                .setParameter("eyeColor", eyeColorText)
                .setParameter("eyeColors", eyeColors)
                .setParameter("hairColor", hairColorText)
                .setParameter("hairColors", hairColors)
                .setParameter("nationality", nationalityText)
                .setParameter("nationalities", nationalities)
                .setFirstResult((int) Math.min(offset, Integer.MAX_VALUE))
                .setMaxResults(query.getPageSize())
                .getResultList();
    }

    /**
     * Считает персоны с фильтрами по подстроке.
     *
     * @param eyeColor подстрока цвета глаз или null
     * @param hairColor подстрока цвета волос или null
     * @param nationality подстрока национальности или null
     * @return количество
     */
    public long count(String eyeColor, String hairColor, String nationality) {
        String eyeColorText = eyeColor == null ? "" : eyeColor.toLowerCase(Locale.ROOT);
        String hairColorText = hairColor == null ? "" : hairColor.toLowerCase(Locale.ROOT);
        String nationalityText = nationality == null ? "" : nationality.toLowerCase(Locale.ROOT);
        List<Color> eyeColors = Stream.of(Color.values())
                .filter(value -> value.name().toLowerCase(Locale.ROOT).contains(eyeColorText))
                .toList();
        List<Color> hairColors = Stream.of(Color.values())
                .filter(value -> value.name().toLowerCase(Locale.ROOT).contains(hairColorText))
                .toList();
        List<Country> nationalities = Stream.of(Country.values())
                .filter(value -> value.name().toLowerCase(Locale.ROOT).contains(nationalityText))
                .toList();

        if (eyeColors.isEmpty() || hairColors.isEmpty() || nationalities.isEmpty()) {
            return 0;
        }

        return entityManager.createQuery("SELECT COUNT(p) FROM Person p"
                        + " WHERE (:eyeColor = '' OR p.eyeColor IN :eyeColors)"
                        + " AND (:hairColor = '' OR p.hairColor IN :hairColors)"
                        + " AND (:nationality = '' OR p.nationality IN :nationalities)",
                        Long.class)
                .setParameter("eyeColor", eyeColorText)
                .setParameter("eyeColors", eyeColors)
                .setParameter("hairColor", hairColorText)
                .setParameter("hairColors", hairColors)
                .setParameter("nationality", nationalityText)
                .setParameter("nationalities", nationalities)
                .getSingleResult();
    }

    /**
     * Сохраняет новую персону.
     *
     * @param person персона
     */
    public void save(Person person) {
        entityManager.persist(person);
    }

    /**
     * Отвязывает персону от работника.
     *
     * @param person персона
     */
    public void unlink(Person person) {
        person.setWorker(null);
        entityManager.flush();
    }

    /**
     * Удаляет персону.
     *
     * @param person персона
     */
    public void delete(Person person) {
        entityManager.remove(person);
    }
}
