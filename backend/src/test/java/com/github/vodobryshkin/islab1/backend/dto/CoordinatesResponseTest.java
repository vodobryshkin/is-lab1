package com.github.vodobryshkin.islab1.backend.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.vodobryshkin.islab1.backend.ConstraintViolations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Проверяет границы координат ответа и их JSON.
 */
class CoordinatesResponseTest {
    private final ConstraintViolations violations = new ConstraintViolations();
    private final JsonOf json = new JsonOf();

    @AfterEach
    void close() {
        violations.close();
        json.close();
    }

    @Test
    void accepts_minimum_coordinates() {
        assertThat(violations.of(new CoordinatesResponse(1L, -953, -391L))).isEmpty();
    }

    @Test
    void rejects_x_equal_to_minus_954() {
        assertThat(violations.of(new CoordinatesResponse(1L, -954, 0L))).containsExactly("x Min");
    }

    @Test
    void rejects_y_equal_to_minus_392() {
        assertThat(violations.of(new CoordinatesResponse(1L, 0, -392L))).containsExactly("y Min");
    }

    @Test
    void rejects_missing_id() {
        assertThat(violations.of(new CoordinatesResponse(null, 0, 0L))).containsExactly("id NotNull");
    }

    @Test
    void writes_contract_json() {
        assertThat(json.serialize(new CoordinatesResponse(5L, -953, -391L)))
                .isEqualTo(json.parse("{\"id\":5,\"x\":-953,\"y\":-391}"));
    }
}
