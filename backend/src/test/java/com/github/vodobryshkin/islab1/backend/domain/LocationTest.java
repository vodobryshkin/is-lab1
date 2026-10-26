package com.github.vodobryshkin.islab1.backend.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.vodobryshkin.islab1.backend.ConstraintViolations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Проверяет обязательность координат локации.
 */
class LocationTest {
    private final ConstraintViolations violations = new ConstraintViolations();

    @AfterEach
    void closeValidator() {
        violations.close();
    }

    @Test
    void accepts_location_with_type_extremes() {
        assertThat(violations.of(new Location(null, Integer.MIN_VALUE, Integer.MAX_VALUE, Long.MIN_VALUE)))
                .isEmpty();
    }

    @Test
    void rejects_missing_x() {
        assertThat(violations.of(new Location(null, null, 0, 0L))).containsExactly("x NotNull");
    }

    @Test
    void rejects_missing_y() {
        assertThat(violations.of(new Location(null, 0, null, 0L))).containsExactly("y NotNull");
    }

    @Test
    void rejects_missing_z() {
        assertThat(violations.of(new Location(null, 0, 0, null))).containsExactly("z NotNull");
    }
}
