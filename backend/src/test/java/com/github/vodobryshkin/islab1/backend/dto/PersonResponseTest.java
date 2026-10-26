package com.github.vodobryshkin.islab1.backend.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.vodobryshkin.islab1.backend.ConstraintViolations;
import com.github.vodobryshkin.islab1.backend.domain.Color;
import com.github.vodobryshkin.islab1.backend.domain.Country;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Проверяет конечный положительный вес персоны ответа и её JSON.
 */
class PersonResponseTest {
    private static final String WEIGHT_POSITIVE = "weight Positive";

    private final ConstraintViolations violations = new ConstraintViolations();
    private final JsonOf json = new JsonOf();

    @AfterEach
    void close() {
        violations.close();
        json.close();
    }

    @Test
    void accepts_smallest_positive_weight() {
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
        assertThat(violations.of(person(Float.POSITIVE_INFINITY))).containsExactly("weight DecimalMax");
    }

    @Test
    void rejects_weight_which_is_negative_infinity() {
        assertThat(violations.of(person(Float.NEGATIVE_INFINITY))).containsExactly(WEIGHT_POSITIVE);
    }

    @Test
    void validates_location_in_cascade() {
        PersonResponse sut = new PersonResponse(1L, null, Color.BLACK,
                new LocationResponse(2L, null, 1, 1L), ZonedDateTime.parse("2000-05-06T07:08:09Z"),
                70.5f, Country.RUSSIA);

        assertThat(violations.of(sut)).containsExactly("location.x NotNull");
    }

    @Test
    void writes_contract_json_with_null_eye_color() {
        assertThat(json.serialize(person(70.5f))).isEqualTo(json.parse(
                """
                {"id":1,"eye_color":null,"hair_color":"BLACK",
                 "location":{"id":2,"x":1,"y":1,"z":1},
                 "birthday":"2000-05-06T07:08:09Z","weight":70.5,"nationality":"RUSSIA"}
                """));
    }

    private PersonResponse person(Float weight) {
        return new PersonResponse(1L, null, Color.BLACK, new LocationResponse(2L, 1, 1, 1L),
                ZonedDateTime.parse("2000-05-06T07:08:09Z"), weight, Country.RUSSIA);
    }
}
