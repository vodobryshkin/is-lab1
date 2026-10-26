package com.github.vodobryshkin.islab1.backend.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.vodobryshkin.islab1.backend.ConstraintViolations;
import com.github.vodobryshkin.islab1.backend.domain.Color;
import com.github.vodobryshkin.islab1.backend.domain.Country;
import com.github.vodobryshkin.islab1.backend.domain.Position;
import com.github.vodobryshkin.islab1.backend.domain.Status;
import jakarta.json.JsonObject;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Проверяет конечную положительную зарплату работника ответа, каскадную проверку
 * вложенных объектов и JSON по контракту.
 */
class WorkerResponseTest {
    private static final ZonedDateTime DATE = ZonedDateTime.parse("2026-10-16T00:00:00+03:00");
    private static final String SALARY_POSITIVE = "salary Positive";

    private final ConstraintViolations violations = new ConstraintViolations();
    private final JsonOf json = new JsonOf();

    @AfterEach
    void close() {
        violations.close();
        json.close();
    }

    @Test
    void accepts_smallest_positive_salary() {
        assertThat(violations.of(worker(Double.MIN_VALUE, coordinates(), null))).isEmpty();
    }

    @Test
    void rejects_zero_salary() {
        assertThat(violations.of(worker(0, coordinates(), null))).containsExactly(SALARY_POSITIVE);
    }

    @Test
    void rejects_negative_salary() {
        assertThat(violations.of(worker(-1, coordinates(), null))).containsExactly(SALARY_POSITIVE);
    }

    @Test
    void rejects_salary_which_is_nan() {
        assertThat(violations.of(worker(Double.NaN, coordinates(), null))).contains(SALARY_POSITIVE);
    }

    @Test
    void rejects_salary_which_is_positive_infinity() {
        assertThat(violations.of(worker(Double.POSITIVE_INFINITY, coordinates(), null)))
                .containsExactly("salary DecimalMax");
    }

    @Test
    void rejects_salary_which_is_negative_infinity() {
        assertThat(violations.of(worker(Double.NEGATIVE_INFINITY, coordinates(), null)))
                .containsExactly(SALARY_POSITIVE);
    }

    @Test
    void validates_coordinates_in_cascade() {
        assertThat(violations.of(worker(1, new CoordinatesResponse(1L, -954, 0L), null)))
                .containsExactly("coordinates.x Min");
    }

    @Test
    void validates_person_in_cascade() {
        PersonResponse person = new PersonResponse(3L, null, Color.BLACK,
                new LocationResponse(2L, 1, 1, 1L), DATE, 0f, Country.RUSSIA);

        assertThat(violations.of(worker(1, coordinates(), person)))
                .containsExactly("person.weight Positive");
    }

    @Test
    void writes_contract_json_with_null_optional_fields() {
        assertThat(json.serialize(worker(12.5, coordinates(), null))).isEqualTo(json.parse(
                """
                {"id":42,"name":"Иван Иванов","coordinates":{"id":1,"x":10,"y":20},
                 "creation_date":"2026-10-16T00:00:00+03:00","organization":null,
                 "salary":12.5,"rating":7,"start_date":"2026-10-16T00:00:00+03:00",
                 "position":null,"status":null,"person":null,"creator_id":9}
                """));
    }

    @Test
    void writes_enums_by_name() {
        WorkerResponse sut = new WorkerResponse(42, "Иван Иванов", coordinates(), DATE, null, 12.5,
                7, DATE, Position.DIRECTOR, Status.HIRED, null, 9L);

        JsonObject actual = json.serialize(sut);

        assertThat(List.of(actual.getString("position"), actual.getString("status")))
                .containsExactly("DIRECTOR", "HIRED");
    }

    private WorkerResponse worker(double salary, CoordinatesResponse coordinates,
            PersonResponse person) {
        return new WorkerResponse(42, "Иван Иванов", coordinates, DATE, null, salary, 7, DATE,
                null, null, person, 9L);
    }

    private CoordinatesResponse coordinates() {
        return new CoordinatesResponse(1L, 10, 20L);
    }
}
