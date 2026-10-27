package com.github.vodobryshkin.islab1.backend.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.vodobryshkin.islab1.backend.ConstraintViolations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Проверяет адрес ответа: непустую улицу, город и JSON с town: null.
 */
class AddressResponseTest {
    private final ConstraintViolations violations = new ConstraintViolations();
    private final JsonOf json = new JsonOf();

    @AfterEach
    void close() {
        violations.close();
        json.close();
    }

    @Test
    void rejects_street_of_spaces() {
        assertThat(violations.of(new AddressResponse(1L, " ", null))).containsExactly("street NotBlank");
    }

    @Test
    void validates_town_in_cascade() {
        assertThat(violations.of(new AddressResponse(1L, "Kronverksky",
                new LocationResponse(2L, null, 1, 1L)))).containsExactly("town.x NotNull");
    }

    @Test
    void writes_null_town_as_contract_requires() {
        assertThat(json.serialize(new AddressResponse(3L, "Kronverksky", null)))
                .isEqualTo(json.parse("{\"id\":3,\"street\":\"Kronverksky\",\"town\":null}"));
    }

    @Test
    void writes_nested_town() {
        assertThat(json.serialize(new AddressResponse(3L, "Kronverksky",
                new LocationResponse(2L, 1, 2, 3L))))
                .isEqualTo(json.parse("{\"id\":3,\"street\":\"Kronverksky\","
                        + "\"town\":{\"id\":2,\"x\":1,\"y\":2,\"z\":3}}"));
    }
}
