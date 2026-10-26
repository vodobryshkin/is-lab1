package com.github.vodobryshkin.islab1.backend.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.vodobryshkin.islab1.backend.ConstraintViolations;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Проверяет ограничения работника: конечная положительная зарплата, имя, рейтинг,
 * обязательные связи и каскадную проверку вложенных объектов.
 */
class WorkerTest {
    private static final ZonedDateTime DATE = ZonedDateTime.parse("2026-01-02T03:04:05+03:00");
    private static final String SALARY_POSITIVE = "salary Positive";
    private static final String NAME = "Иван Иванов";

    private final ConstraintViolations violations = new ConstraintViolations();

    @AfterEach
    void closeValidator() {
        violations.close();
    }

    @Test
    void accepts_worker_without_optional_fields() {
        assertThat(violations.of(worker(NAME, Double.MIN_VALUE, 1, coordinates(), null))).isEmpty();
    }

    @Test
    void accepts_name_of_255_characters() {
        assertThat(violations.of(worker("n".repeat(255), 1, 1, coordinates(), null))).isEmpty();
    }

    @Test
    void rejects_name_of_256_characters() {
        assertThat(violations.of(worker("n".repeat(256), 1, 1, coordinates(), null)))
                .contains("name Size");
    }

    @Test
    void rejects_name_with_symbols_outside_contract_pattern() {
        assertThat(violations.of(worker("Иван!", 1, 1, coordinates(), null)))
                .containsExactly("name Pattern");
    }

    @Test
    void rejects_name_of_spaces() {
        assertThat(violations.of(worker(" ", 1, 1, coordinates(), null)))
                .containsExactly("name NotBlank");
    }

    @Test
    void rejects_zero_salary() {
        assertThat(violations.of(worker(NAME, 0, 1, coordinates(), null)))
                .containsExactly(SALARY_POSITIVE);
    }

    @Test
    void rejects_negative_salary() {
        assertThat(violations.of(worker(NAME, -1, 1, coordinates(), null)))
                .containsExactly(SALARY_POSITIVE);
    }

    @Test
    void rejects_salary_which_is_nan() {
        assertThat(violations.of(worker(NAME, Double.NaN, 1, coordinates(), null)))
                .contains(SALARY_POSITIVE);
    }

    @Test
    void rejects_salary_which_is_positive_infinity() {
        assertThat(violations.of(worker(NAME, Double.POSITIVE_INFINITY, 1, coordinates(), null)))
                .containsExactly("salary DecimalMax");
    }

    @Test
    void rejects_salary_which_is_negative_infinity() {
        assertThat(violations.of(worker(NAME, Double.NEGATIVE_INFINITY, 1, coordinates(), null)))
                .containsExactly(SALARY_POSITIVE);
    }

    @Test
    void rejects_zero_rating() {
        assertThat(violations.of(worker(NAME, 1, 0, coordinates(), null)))
                .containsExactly("rating Positive");
    }

    @Test
    void generates_creation_date_before_saving() {
        Worker sut = new Worker(null, new User(null, "alice", "hash", null), NAME, coordinates(),
                null, null, 1, 1, DATE, null, null, null);

        sut.onCreate();

        assertThat(violations.of(sut)).isEmpty();
    }

    @Test
    void rejects_missing_required_fields() {
        Worker sut = new Worker(null, null, null, null, null, null, 1, null, null, null, null, null);

        assertThat(violations.of(sut)).containsExactlyInAnyOrder("user NotNull", "name NotBlank",
                "coordinates NotNull", "creationDate NotNull", "rating NotNull", "startDate NotNull");
    }

    @Test
    void validates_coordinates_in_cascade() {
        assertThat(violations.of(worker(NAME, 1, 1, new Coordinates(null, -954, 0L), null)))
                .containsExactly("coordinates.x Min");
    }

    @Test
    void validates_organization_in_cascade() {
        Address address = new Address(null, "Kronverksky", null);
        Organization organization =
                new Organization(null, address, 1f, 1L, " ", 1, null, address);

        assertThat(violations.of(worker(NAME, 1, 1, coordinates(), organization)))
                .containsExactly("organization.fullName NotBlank");
    }

    @Test
    void validates_user_in_cascade() {
        Worker sut = new Worker(null, new User(null, "ab", "hash", null), NAME, coordinates(), DATE,
                null, 1, 1, DATE, null, null, null);

        assertThat(violations.of(sut)).containsExactly("user.username Pattern");
    }

    private Worker worker(String name, double salary, Integer rating, Coordinates coordinates,
            Organization organization) {
        return new Worker(null, new User(null, "alice", "hash", null), name, coordinates, DATE,
                organization, salary, rating, DATE, null, null, null);
    }

    private Coordinates coordinates() {
        return new Coordinates(null, 1, 1L);
    }
}
