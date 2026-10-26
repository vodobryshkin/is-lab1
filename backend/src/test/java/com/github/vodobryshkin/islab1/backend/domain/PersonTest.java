package com.github.vodobryshkin.islab1.backend.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.vodobryshkin.islab1.backend.ConstraintViolations;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Проверяет ограничения персоны: конечный положительный вес и обязательные поля.
 */
class PersonTest {
    private static final ZonedDateTime BIRTHDAY = ZonedDateTime.parse("2000-05-06T07:08:09Z");
    private static final String WEIGHT_POSITIVE = "weight Positive";

    private final ConstraintViolations violations = new ConstraintViolations();

    @AfterEach
    void closeValidator() {
        violations.close();
    }

    @Test
    void accepts_person_without_eye_color_and_worker() {
        assertThat(violations.of(person(Float.MIN_VALUE))).isEmpty();
    }

    @Test
    void rejects_zero_weight() {
        assertThat(violations.of(person(0f))).containsExactly(WEIGHT_POSITIVE);
    }

    @Test
    void rejects_negative_weight() {
        assertThat(violations.of(person(-1f))).containsExactly(WEIGHT_POSITIVE);
    }

    @Test
    void rejects_weight_which_is_nan() {
        assertThat(violations.of(person(Float.NaN))).contains(WEIGHT_POSITIVE);
    }

    @Test
    void rejects_weight_which_is_positive_infinity() {
        assertThat(violations.of(person(Float.POSITIVE_INFINITY)))
                .containsExactly("weight DecimalMax");
    }

    @Test
    void rejects_weight_which_is_negative_infinity() {
        assertThat(violations.of(person(Float.NEGATIVE_INFINITY))).containsExactly(WEIGHT_POSITIVE);
    }

    @Test
    void rejects_missing_required_fields() {
        Person sut = new Person(null, null, null, null, null, null, null, null);

        assertThat(violations.of(sut)).containsExactlyInAnyOrder("hairColor NotNull",
                "location NotNull", "birthday NotNull", "weight NotNull", "nationality NotNull");
    }

    @Test
    void validates_location_in_cascade() {
        Person sut = new Person(null, null, Color.BLACK, new Location(null, null, 1, 1L), BIRTHDAY,
                70f, Country.RUSSIA, null);

        assertThat(violations.of(sut)).containsExactly("location.x NotNull");
    }

    private Person person(Float weight) {
        return new Person(null, null, Color.BLACK, new Location(null, 1, 1, 1L), BIRTHDAY, weight,
                Country.RUSSIA, null);
    }
}
