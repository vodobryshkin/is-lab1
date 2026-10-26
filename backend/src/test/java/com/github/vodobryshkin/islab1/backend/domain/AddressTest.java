package com.github.vodobryshkin.islab1.backend.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.vodobryshkin.islab1.backend.ConstraintViolations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Проверяет непустую улицу и каскадную проверку необязательного города.
 */
class AddressTest {
    private final ConstraintViolations violations = new ConstraintViolations();

    @AfterEach
    void closeValidator() {
        violations.close();
    }

    @Test
    void accepts_address_with_town() {
        assertThat(violations.of(new Address(null, "Kronverksky", new Location(null, 1, 2, 3L))))
                .isEmpty();
    }

    @Test
    void accepts_address_without_town() {
        assertThat(violations.of(new Address(null, "Kronverksky", null))).isEmpty();
    }

    @Test
    void rejects_missing_street() {
        assertThat(violations.of(new Address(null, null, null))).containsExactly("street NotBlank");
    }

    @Test
    void rejects_street_of_spaces() {
        assertThat(violations.of(new Address(null, " \t", null))).containsExactly("street NotBlank");
    }

    @Test
    void validates_town_in_cascade() {
        assertThat(violations.of(new Address(null, "Kronverksky", new Location(null, null, 2, 3L))))
                .containsExactly("town.x NotNull");
    }
}
