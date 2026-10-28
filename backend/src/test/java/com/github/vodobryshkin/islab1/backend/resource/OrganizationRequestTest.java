package com.github.vodobryshkin.islab1.backend.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.github.vodobryshkin.islab1.backend.domain.Organization;
import com.github.vodobryshkin.islab1.backend.domain.OrganizationType;
import com.github.vodobryshkin.islab1.backend.dto.common.ErrorResponse;
import com.github.vodobryshkin.islab1.backend.dto.organization.OrganizationRequest;
import com.github.vodobryshkin.islab1.backend.dto.common.ValidationErrorResponse;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.ws.rs.WebApplicationException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Проверяет чтение организации из JSON: варианты адресов, обязательные поля и имена нарушений.
 */
class OrganizationRequestTest {
    private static final String FIELDS =
            """
            "annual_turnover": 1.5, "employees_count": 3, "full_name": "ООО Ромашка",
            "rating": 5, "type": "TRUST"
            """;
    private static final String NEW_OFFICIAL =
            "\"official_address\": {\"street\": \"Kronverksky\", \"town\": null}";
    private static final String NEW_POSTAL =
            "\"postal_address\": {\"street\": \"Lomonosova\", \"town\": {\"x\": 1, \"y\": 2, \"z\": 3}}";

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final RequestReader reader = new RequestReader(factory.getValidator());

    @AfterEach
    void closeFactory() {
        factory.close();
    }

    @Test
    void reads_new_official_and_postal_addresses() {
        // Act
        Organization actual = read(FIELDS + ", " + NEW_OFFICIAL + ", " + NEW_POSTAL);

        // Assert
        assertAll(
                () -> assertThat(actual.getOfficialAddress().getId()).isNull(),
                () -> assertThat(actual.getOfficialAddress().getStreet()).isEqualTo("Kronverksky"),
                () -> assertThat(actual.getOfficialAddress().getTown()).isNull(),
                () -> assertThat(actual.getPostalAddress().getTown().getZ()).isEqualTo(3L),
                () -> assertThat(actual.getAnnualTurnover()).isEqualTo(1.5f),
                () -> assertThat(actual.getType()).isEqualTo(OrganizationType.TRUST)
        );
    }

    @Test
    void reads_ids_of_existing_addresses() {
        // Act
        Organization actual = read(FIELDS + ", \"official_address_id\": 10, \"postal_address_id\": 20");

        // Assert
        assertAll(
                () -> assertThat(actual.getOfficialAddress().getId()).isEqualTo(10L),
                () -> assertThat(actual.getPostalAddress().getId()).isEqualTo(20L)
        );
    }

    @Test
    void reads_new_official_address_with_existing_postal_one() {
        // Act
        Organization actual = read(FIELDS + ", " + NEW_OFFICIAL + ", \"postal_address_id\": 20");

        // Assert
        assertAll(
                () -> assertThat(actual.getOfficialAddress().getStreet()).isEqualTo("Kronverksky"),
                () -> assertThat(actual.getPostalAddress().getId()).isEqualTo(20L)
        );
    }

    @Test
    void reads_existing_official_address_with_new_postal_one() {
        // Act
        Organization actual = read(FIELDS + ", \"official_address_id\": 10, " + NEW_POSTAL);

        // Assert
        assertAll(
                () -> assertThat(actual.getOfficialAddress().getId()).isEqualTo(10L),
                () -> assertThat(actual.getPostalAddress().getStreet()).isEqualTo("Lomonosova")
        );
    }

    @Test
    void accepts_null_type() {
        // Act
        Organization actual = read(FIELDS.replace("\"TRUST\"", "null") + ", " + NEW_OFFICIAL
                + ", " + NEW_POSTAL);

        // Assert
        assertThat(actual.getType()).isNull();
    }

    @Test
    void rejects_new_address_together_with_its_id() {
        // Act
        List<String> actual = issues(FIELDS + ", " + NEW_OFFICIAL
                + ", \"official_address_id\": 10, \"postal_address_id\": 20");

        // Assert
        assertThat(actual).containsExactly("official_address INVALID_COMBINATION");
    }

    @Test
    void requires_one_of_address_variants() {
        // Act
        List<String> actual = issues(FIELDS + ", \"postal_address_id\": 20");

        // Assert
        assertThat(actual).containsExactly("official_address INVALID_COMBINATION");
    }

    @Test
    void rejects_null_address_and_non_positive_address_id() {
        // Act
        List<String> actual = issues(FIELDS + ", \"official_address\": null, \"postal_address_id\": 0");

        // Assert
        assertThat(actual).containsExactlyInAnyOrder("official_address INVALID_COMBINATION",
                "postal_address_id MUST_BE_POSITIVE");
    }

    @Test
    void reports_nested_address_errors_with_contract_names() {
        // Act
        List<String> actual = issues(FIELDS + ", \"official_address\": {\"street\": \" \", "
                + "\"town\": {\"x\": null, \"y\": 1, \"z\": 1}}, \"postal_address_id\": 20");

        // Assert
        assertThat(actual).containsExactlyInAnyOrder("official_address.street MUST_NOT_BE_EMPTY",
                "official_address.town.x MUST_NOT_BE_NULL");
    }

    @Test
    void reports_rule_violations_in_snake_case() {
        // Act
        List<String> actual = issues("\"annual_turnover\": 0, \"employees_count\": 0, "
                + "\"full_name\": \" \", \"rating\": 0, \"type\": null, "
                + NEW_OFFICIAL + ", " + NEW_POSTAL);

        // Assert
        assertThat(actual).containsExactlyInAnyOrder("annual_turnover MUST_BE_POSITIVE",
                "employees_count MUST_BE_POSITIVE", "full_name MUST_NOT_BE_EMPTY",
                "rating MUST_BE_POSITIVE");
    }

    @Test
    void reports_missing_and_out_of_range_fields() {
        // Act
        List<String> actual = issues("\"annual_turnover\": 1e39, \"full_name\": null, "
                + "\"rating\": 5, \"flag\": true, " + NEW_OFFICIAL + ", " + NEW_POSTAL);

        // Assert
        assertThat(actual).containsExactlyInAnyOrder("annual_turnover OUT_OF_RANGE",
                "employees_count MUST_NOT_BE_NULL", "full_name MUST_NOT_BE_EMPTY");
    }

    @Test
    void rejects_unknown_enum_value_with_400() {
        // Act
        WebApplicationException actual = catchThrowableOfType(WebApplicationException.class,
                () -> read(FIELDS.replace("TRUST", "STATE") + ", " + NEW_OFFICIAL + ", " + NEW_POSTAL));

        // Assert
        assertThat(actual.getResponse().getStatus()).isEqualTo(400);
    }

    @Test
    void rejects_fractional_and_too_large_integers_with_400() {
        // Act
        WebApplicationException fractional = catchThrowableOfType(WebApplicationException.class,
                () -> read(FIELDS.replace("\"rating\": 5", "\"rating\": 1.5") + ", " + NEW_OFFICIAL + ", " + NEW_POSTAL));
        WebApplicationException large = catchThrowableOfType(WebApplicationException.class,
                () -> read(FIELDS.replace("\"rating\": 5", "\"rating\": 4294967297") + ", " + NEW_OFFICIAL + ", " + NEW_POSTAL));

        // Assert
        assertAll(
                () -> assertThat(fractional.getResponse().getStatus()).isEqualTo(400),
                () -> assertThat(large.getResponse().getStatus()).isEqualTo(400)
        );
    }

    @Test
    void rejects_string_instead_of_number_with_400() {
        // Act
        WebApplicationException actual = catchThrowableOfType(WebApplicationException.class,
                () -> read(FIELDS.replace("\"rating\": 5", "\"rating\": \"5\"") + ", " + NEW_OFFICIAL + ", " + NEW_POSTAL));

        // Assert
        assertThat(actual.getResponse().getStatus()).isEqualTo(400);
    }

    @Test
    void rejects_number_instead_of_string_with_400() {
        // Act
        WebApplicationException actual = catchThrowableOfType(WebApplicationException.class,
                () -> read(FIELDS.replace("\"ООО Ромашка\"", "5") + ", " + NEW_OFFICIAL + ", " + NEW_POSTAL));

        // Assert
        assertThat(actual.getResponse().getStatus()).isEqualTo(400);
    }

    @Test
    void rejects_string_instead_of_float_with_400() {
        // Act
        WebApplicationException actual = catchThrowableOfType(WebApplicationException.class,
                () -> read(FIELDS.replace("1.5", "\"1.5\"") + ", " + NEW_OFFICIAL + ", " + NEW_POSTAL));

        // Assert
        assertThat(actual.getResponse().getStatus()).isEqualTo(400);
    }

    @Test
    void rejects_malformed_json_with_400() {
        // Act
        WebApplicationException actual = catchThrowableOfType(WebApplicationException.class,
                () -> reader.read("{\"full_name\":", OrganizationRequest.class));

        // Assert
        assertAll(
                () -> assertThat(actual.getResponse().getStatus()).isEqualTo(400),
                () -> assertThat(actual.getResponse().getEntity())
                        .isEqualTo(new ErrorResponse("The request body contains invalid JSON or a value of the wrong type."))
        );
    }

    private Organization read(String fields) {
        return reader.read("{" + fields + "}", OrganizationRequest.class).toEntity();
    }

    private List<String> issues(String fields) {
        WebApplicationException exception =
                catchThrowableOfType(WebApplicationException.class, () -> read(fields));
        ValidationErrorResponse response = (ValidationErrorResponse) exception.getResponse().getEntity();

        return response.getErrors().stream()
                .map(issue -> issue.getField() + " " + issue.getCode())
                .toList();
    }
}
