package com.github.vodobryshkin.islab1.backend.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.vodobryshkin.islab1.backend.ConstraintViolations;
import com.github.vodobryshkin.islab1.backend.dto.location.LocationResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Проверяет обязательные поля локации ответа и её JSON.
 */
class LocationResponseTest {
    private final ConstraintViolations violations = new ConstraintViolations();
    private final JsonOf json = new JsonOf();

    @AfterEach
    void close() {
        violations.close();
        json.close();
    }

    @Test
    void rejects_missing_coordinates() {
        assertThat(violations.of(new LocationResponse(1L, null, null, null)))
                .containsExactlyInAnyOrder("x NotNull", "y NotNull", "z NotNull");
    }

    @Test
    void writes_contract_json() {
        assertThat(json.serialize(new LocationResponse(7L, 1, -2, Long.MAX_VALUE)))
                .isEqualTo(json.parse("{\"id\":7,\"x\":1,\"y\":-2,\"z\":9223372036854775807}"));
    }
}
