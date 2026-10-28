package com.github.vodobryshkin.islab1.backend.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.vodobryshkin.islab1.backend.ConstraintViolations;
import com.github.vodobryshkin.islab1.backend.domain.OrganizationType;
import com.github.vodobryshkin.islab1.backend.dto.address.AddressResponse;
import com.github.vodobryshkin.islab1.backend.dto.organization.OrganizationResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Проверяет конечный положительный оборот организации ответа и её JSON.
 */
class OrganizationResponseTest {
    private static final String TURNOVER_POSITIVE = "annualTurnover Positive";

    private final ConstraintViolations violations = new ConstraintViolations();
    private final JsonOf json = new JsonOf();

    @AfterEach
    void close() {
        violations.close();
        json.close();
    }

    @Test
    void accepts_smallest_positive_turnover() {
        assertThat(violations.of(organization(Float.MIN_VALUE, null))).isEmpty();
    }

    @Test
    void rejects_zero_turnover() {
        assertThat(violations.of(organization(0f, null))).containsExactly(TURNOVER_POSITIVE);
    }

    @Test
    void rejects_negative_turnover() {
        assertThat(violations.of(organization(-1f, null))).containsExactly(TURNOVER_POSITIVE);
    }

    @Test
    void rejects_turnover_which_is_nan() {
        assertThat(violations.of(organization(Float.NaN, null))).contains(TURNOVER_POSITIVE);
    }

    @Test
    void rejects_turnover_which_is_positive_infinity() {
        assertThat(violations.of(organization(Float.POSITIVE_INFINITY, null)))
                .containsExactly("annualTurnover DecimalMax");
    }

    @Test
    void rejects_turnover_which_is_negative_infinity() {
        assertThat(violations.of(organization(Float.NEGATIVE_INFINITY, null)))
                .containsExactly(TURNOVER_POSITIVE);
    }

    @Test
    void validates_addresses_in_cascade() {
        AddressResponse address = new AddressResponse(1L, " ", null);
        OrganizationResponse sut =
                new OrganizationResponse(1L, address, 1f, 1L, "Org", 1, null, address);

        assertThat(violations.of(sut)).containsExactlyInAnyOrder("officialAddress.street NotBlank",
                "postalAddress.street NotBlank");
    }

    @Test
    void writes_contract_json_with_null_type() {
        assertThat(json.serialize(organization(1.5f, null))).isEqualTo(json.parse(
                """
                {"id":4,"official_address":{"id":1,"street":"Kronverksky","town":null},
                 "annual_turnover":1.5,"employees_count":3,"full_name":"ООО Ромашка",
                 "rating":5,"type":null,
                 "postal_address":{"id":1,"street":"Kronverksky","town":null}}
                """));
    }

    @Test
    void writes_type_as_enum_name() {
        assertThat(json.serialize(organization(1.5f, OrganizationType.TRUST)).getString("type"))
                .isEqualTo("TRUST");
    }

    private OrganizationResponse organization(float turnover, OrganizationType type) {
        AddressResponse address = new AddressResponse(1L, "Kronverksky", null);

        return new OrganizationResponse(4L, address, turnover, 3L, "ООО Ромашка", 5, type, address);
    }
}
