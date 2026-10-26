package com.github.vodobryshkin.islab1.backend.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.vodobryshkin.islab1.backend.ConstraintViolations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Проверяет ограничения координат: x > -954, y > -392, оба обязательны.
 */
class CoordinatesTest {
    private final ConstraintViolations violations = new ConstraintViolations();

    @AfterEach
    void closeValidator() {
        violations.close();
    }

    @Test
    void accepts_minimum_coordinates_without_generated_id() {
        assertThat(violations.of(new Coordinates(null, -953, -391L))).isEmpty();
    }

    @Test
    void rejects_x_equal_to_minus_954() {
        assertThat(violations.of(new Coordinates(null, -954, 0L))).containsExactly("x Min");
    }

    @Test
    void rejects_y_equal_to_minus_392() {
        assertThat(violations.of(new Coordinates(null, 0, -392L))).containsExactly("y Min");
    }

    @Test
    void rejects_missing_x() {
        assertThat(violations.of(new Coordinates(null, null, 0L))).containsExactly("x NotNull");
    }

    @Test
    void rejects_missing_y() {
        assertThat(violations.of(new Coordinates(null, 0, null))).containsExactly("y NotNull");
    }

    @Test
    void rejects_id_which_is_zero() {
        assertThat(violations.of(new Coordinates(0L, 0, 0L))).containsExactly("id Positive");
    }
}
