package com.github.vodobryshkin.islab1.backend.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.vodobryshkin.islab1.backend.ConstraintViolations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Проверяет ограничения организации: конечный положительный оборот, обязательные адреса,
 * положительные число сотрудников и рейтинг, непустое полное название.
 */
class OrganizationTest {
    private static final String TURNOVER_POSITIVE = "annualTurnover Positive";
    private static final String NAME = "Org";

    private final ConstraintViolations violations = new ConstraintViolations();

    @AfterEach
    void closeValidator() {
        violations.close();
    }

    @Test
    void accepts_organization_without_type() {
        assertThat(violations.of(organization(Float.MIN_VALUE, 1L, "ООО Ромашка", 1, address())))
                .isEmpty();
    }

    @Test
    void rejects_zero_turnover() {
        assertThat(violations.of(organization(0f, 1L, NAME, 1, address())))
                .containsExactly(TURNOVER_POSITIVE);
    }

    @Test
    void rejects_negative_turnover() {
        assertThat(violations.of(organization(-1f, 1L, NAME, 1, address())))
                .containsExactly(TURNOVER_POSITIVE);
    }

    @Test
    void rejects_turnover_which_is_nan() {
        assertThat(violations.of(organization(Float.NaN, 1L, NAME, 1, address())))
                .contains(TURNOVER_POSITIVE);
    }

    @Test
    void rejects_turnover_which_is_positive_infinity() {
        assertThat(violations.of(organization(Float.POSITIVE_INFINITY, 1L, NAME, 1, address())))
                .containsExactly("annualTurnover DecimalMax");
    }

    @Test
    void rejects_turnover_which_is_negative_infinity() {
        assertThat(violations.of(organization(Float.NEGATIVE_INFINITY, 1L, NAME, 1, address())))
                .containsExactly(TURNOVER_POSITIVE);
    }

    @Test
    void rejects_zero_employees_count() {
        assertThat(violations.of(organization(1f, 0L, NAME, 1, address())))
                .containsExactly("employeesCount Positive");
    }

    @Test
    void rejects_missing_employees_count() {
        assertThat(violations.of(organization(1f, null, NAME, 1, address())))
                .containsExactly("employeesCount NotNull");
    }

    @Test
    void rejects_full_name_of_spaces() {
        assertThat(violations.of(organization(1f, 1L, " ", 1, address())))
                .containsExactly("fullName NotBlank");
    }

    @Test
    void rejects_zero_rating() {
        assertThat(violations.of(organization(1f, 1L, NAME, 0, address())))
                .containsExactly("rating Positive");
    }

    @Test
    void rejects_missing_rating() {
        assertThat(violations.of(organization(1f, 1L, NAME, null, address())))
                .containsExactly("rating NotNull");
    }

    @Test
    void rejects_missing_addresses() {
        assertThat(violations.of(organization(1f, 1L, NAME, 1, null)))
                .containsExactlyInAnyOrder("officialAddress NotNull", "postalAddress NotNull");
    }

    @Test
    void validates_addresses_in_cascade() {
        assertThat(violations.of(organization(1f, 1L, NAME, 1, new Address(null, " ", null))))
                .containsExactlyInAnyOrder("officialAddress.street NotBlank",
                        "postalAddress.street NotBlank");
    }

    private Organization organization(float turnover, Long employees, String fullName,
            Integer rating, Address address) {
        return new Organization(null, address, turnover, employees, fullName, rating, null, address);
    }

    private Address address() {
        return new Address(null, "Kronverksky", null);
    }
}
