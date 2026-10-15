package com.github.vodobryshkin.islab1.backend.dto;

import com.github.vodobryshkin.islab1.backend.domain.OrganizationType;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import java.io.StringReader;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * Тестовый класс для тестирования класса OrganizationResponse.
 *
 * @author vodobryshkin
 * @since 15.10.2026 23:08
 */
public class OrganizationResponseTest {
    @Test
    void passes_correct_DTO() {
        OrganizationResponse sut = new OrganizationResponse(
                1L,
                new AddressResponse(1L, "postal", null),
                1f,
                1L,
                "ООО Магистраль",
                100,
                OrganizationType.GOVERNMENT,
                new AddressResponse(1L, "official", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            int actualSize  = factory.getValidator().validate(sut).size();

            assertThat(actualSize)
                    .as("If there are no errors in the DTO, then violations should not be thrown out.")
                    .isEqualTo(0);
        }
    }

    @Test
    void passes_correct_DTO_with_atypical_annualTurnover() {
        OrganizationResponse sut = new OrganizationResponse(
                1L,
                new AddressResponse(1L, "postal", null),
                0x0.000002P-126f,
                1L,
                "ООО Магистраль",
                100,
                OrganizationType.GOVERNMENT,
                new AddressResponse(1L, "official", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            int actualSize  = factory.getValidator().validate(sut).size();

            assertThat(actualSize)
                    .as("If there are no errors in the DTO, then violations should not be thrown out.")
                    .isEqualTo(0);
        }
    }

    @Test
    void passes_correct_DTO_with_atypical_fullName() {
        OrganizationResponse sut = new OrganizationResponse(
                1L,
                new AddressResponse(1L, "postal", null),
                1f,
                1L,
                "\uD835\uDD08\uD835\uDD2F\uD835\uDD2F\uD835\uDD2C\uD835\uDD2F ☣ ☠",
                100,
                OrganizationType.GOVERNMENT,
                new AddressResponse(1L, "official", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            int actualSize  = factory.getValidator().validate(sut).size();

            assertThat(actualSize)
                    .as("If there are no errors in the DTO, then violations should not be thrown out.")
                    .isEqualTo(0);
        }
    }

    @Test
    void passes_correct_DTO_with_null_organizationType() {
        OrganizationResponse sut = new OrganizationResponse(
                1L,
                new AddressResponse(1L, "postal", null),
                1f,
                1L,
                "ООО Магистраль",
                100,
                null,
                new AddressResponse(1L, "official", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            int actualSize  = factory.getValidator().validate(sut).size();

            assertThat(actualSize)
                    .as("If there are no errors in the DTO, then violations should not be thrown out.")
                    .isEqualTo(0);
        }
    }

    @Test
    void rejects_id_which_is_null() {
        // Arrange
        OrganizationResponse sut = new OrganizationResponse(
                null,
                new AddressResponse(1L, "postal", null),
                1f,
                1L,
                "ООО Магистраль",
                100,
                null,
                new AddressResponse(1L, "official", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<OrganizationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<OrganizationResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If id is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field id which is null must not be validated.")
                            .isEqualTo("id")
            );
        }
    }

    @Test
    void rejects_id_below_minimum() {
        // Arrange
        OrganizationResponse sut = new OrganizationResponse(
                0L,
                new AddressResponse(1L, "postal", null),
                1f,
                1L,
                "ООО Магистраль",
                100,
                null,
                new AddressResponse(1L, "official", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<OrganizationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<OrganizationResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If id is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field id less than 1 must not be validated.")
                            .isEqualTo("id")
            );
        }
    }

    @Test
    void rejects_id_which_equals_to_long_minimum() {
        // Arrange
        OrganizationResponse sut = new OrganizationResponse(
                Long.MIN_VALUE,
                new AddressResponse(1L, "postal", null),
                1f,
                1L,
                "ООО Магистраль",
                100,
                null,
                new AddressResponse(1L, "official", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<OrganizationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<OrganizationResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If id is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field id equal to Long.MIN_VALUE must not be validated.")
                            .isEqualTo("id")
            );
        }
    }

    @Test
    void rejects_officialAddress_which_is_null() {
        // Arrange
        OrganizationResponse sut = new OrganizationResponse(
                1L,
                null,
                1f,
                1L,
                "ООО Магистраль",
                100,
                null,
                new AddressResponse(1L, "official", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<OrganizationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<OrganizationResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If officialAddress is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Field officialAddress which is null must not be validated.")
                            .isEqualTo("officialAddress")
            );
        }
    }

    @Test
    void validates_incorrect_officialAddress_cascade() {
        // Arrange
        OrganizationResponse sut = new OrganizationResponse(
                1L,
                new AddressResponse(1L, null, null),
                1f,
                1L,
                "ООО Магистраль",
                100,
                null,
                new AddressResponse(1L, "official", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<OrganizationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<OrganizationResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If officialAddress is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Invalid officialAddress must be validated with cascading validation.")
                            .isEqualTo("officialAddress.street")
            );
        }
    }

    @Test
    void rejects_annualTurnover_which_is_NaN() {
        // Arrange
        OrganizationResponse sut = new OrganizationResponse(
                1L,
                new AddressResponse(1L, "postal", null),
                Float.NaN,
                1L,
                "ООО Магистраль",
                100,
                null,
                new AddressResponse(1L, "official", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<OrganizationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<OrganizationResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If annualTurnover is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Field annualTurnover which is NaN must not be validated.")
                            .isEqualTo("annualTurnover")
            );
        }
    }

    @Test
    void rejects_annualTurnover_below_minimum() {
        // Arrange
        OrganizationResponse sut = new OrganizationResponse(
                1L,
                new AddressResponse(1L, "postal", null),
                0,
                1L,
                "ООО Магистраль",
                100,
                null,
                new AddressResponse(1L, "official", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<OrganizationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<OrganizationResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If annualTurnover is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Field annualTurnover which is not positive must not be validated.")
                            .isEqualTo("annualTurnover")
            );
        }
    }

    @Test
    void rejects_annualTurnover_which_equals_to_float_minimum() {
        // Arrange
        OrganizationResponse sut = new OrganizationResponse(
                1L,
                new AddressResponse(1L, "postal", null),
                - Float.MIN_VALUE,
                1L,
                "ООО Магистраль",
                100,
                null,
                new AddressResponse(1L, "official", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<OrganizationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<OrganizationResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If annualTurnover is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Field annualTurnover which is not positive must not be validated.")
                            .isEqualTo("annualTurnover")
            );
        }
    }

    @Test
    void rejects_employeesCount_which_is_null() {
        // Arrange
        OrganizationResponse sut = new OrganizationResponse(
                1L,
                new AddressResponse(1L, "postal", null),
                1f,
                null,
                "ООО Магистраль",
                100,
                null,
                new AddressResponse(1L, "official", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<OrganizationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<OrganizationResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If employeesCount is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Field employeesCount which is null must not be validated.")
                            .isEqualTo("employeesCount")
            );
        }
    }

    @Test
    void rejects_employeesCount_below_minimum() {
        // Arrange
        OrganizationResponse sut = new OrganizationResponse(
                1L,
                new AddressResponse(1L, "postal", null),
                1f,
                0L,
                "ООО Магистраль",
                100,
                null,
                new AddressResponse(1L, "official", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<OrganizationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<OrganizationResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If employeesCount is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Field employeesCount which is not positive must not be validated.")
                            .isEqualTo("employeesCount")
            );
        }
    }

    @Test
    void rejects_employeesCount_which_equals_to_long_minimum() {
        // Arrange
        OrganizationResponse sut = new OrganizationResponse(
                1L,
                new AddressResponse(1L, "postal", null),
                1f,
                Long.MIN_VALUE,
                "ООО Магистраль",
                100,
                null,
                new AddressResponse(1L, "official", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<OrganizationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<OrganizationResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If employeesCount is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Field employeesCount equal to Long.MIN_VALUE must not be validated.")
                            .isEqualTo("employeesCount")
            );
        }
    }

    @Test
    void rejects_fullName_which_is_null() {
        // Arrange
        OrganizationResponse sut = new OrganizationResponse(
                1L,
                new AddressResponse(1L, "postal", null),
                1f,
                1L,
                null,
                100,
                null,
                new AddressResponse(1L, "official", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<OrganizationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<OrganizationResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If fullName is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Field fullName which is null must not be validated.")
                            .isEqualTo("fullName")
            );
        }
    }

    @Test
    void rejects_fullName_which_is_empty() {
        // Arrange
        OrganizationResponse sut = new OrganizationResponse(
                1L,
                new AddressResponse(1L, "postal", null),
                1f,
                1L,
                "",
                100,
                null,
                new AddressResponse(1L, "official", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<OrganizationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<OrganizationResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If fullName is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Field fullName which is empty must not be validated.")
                            .isEqualTo("fullName")
            );
        }
    }

    @Test
    void rejects_rating_which_is_null() {
        // Arrange
        OrganizationResponse sut = new OrganizationResponse(
                1L,
                new AddressResponse(1L, "postal", null),
                1f,
                1L,
                "ООО Магистраль",
                null,
                null,
                new AddressResponse(1L, "official", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<OrganizationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<OrganizationResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If rating is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Field rating which is null must not be validated.")
                            .isEqualTo("rating")
            );
        }
    }

    @Test
    void rejects_rating_below_minimum() {
        // Arrange
        OrganizationResponse sut = new OrganizationResponse(
                1L,
                new AddressResponse(1L, "postal", null),
                1f,
                1L,
                "ООО Магистраль",
                0,
                null,
                new AddressResponse(1L, "official", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<OrganizationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<OrganizationResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If rating is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Field rating which is not positive must not be validated.")
                            .isEqualTo("rating")
            );
        }
    }

    @Test
    void rejects_rating_which_equals_to_integer_minimum() {
        // Arrange
        OrganizationResponse sut = new OrganizationResponse(
                1L,
                new AddressResponse(1L, "postal", null),
                1f,
                1L,
                "ООО Магистраль",
                Integer.MIN_VALUE,
                null,
                new AddressResponse(1L, "official", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<OrganizationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<OrganizationResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If rating is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Field rating equal to Integer.MIN_VALUE must not be validated.")
                            .isEqualTo("rating")
            );
        }
    }

    @Test
    void rejects_postalAddress_which_is_null() {
        // Arrange
        OrganizationResponse sut = new OrganizationResponse(
                1L,
                new AddressResponse(1L, "official", null),
                1f,
                1L,
                "ООО Магистраль",
                100,
                null,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<OrganizationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<OrganizationResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If postalAddress is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Field postalAddress which is null must not be validated.")
                            .isEqualTo("postalAddress")
            );
        }
    }

    @Test
    void validates_incorrect_postalAddress_cascade() {
        // Arrange
        OrganizationResponse sut = new OrganizationResponse(
                1L,
                new AddressResponse(1L, "postal", null),
                1f,
                1L,
                "ООО Магистраль",
                100,
                null,
                new AddressResponse(1L, null, null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<OrganizationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<OrganizationResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If postalAddress is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Invalid postalAddress must be validated with cascading validation.")
                            .isEqualTo("postalAddress.street")
            );
        }
    }

    @Test
    void serializes_organization_with_expected_property_names() throws Exception {
        // Arrange
        OrganizationResponse sut = new OrganizationResponse(
                1L,
                new AddressResponse(1L, "postal", null),
                1f,
                1L,
                "ООО Магистраль",
                100,
                OrganizationType.GOVERNMENT,
                new AddressResponse(1L, "official", null)
        );

        JsonbConfig config = new JsonbConfig().withNullValues(true);

        try (Jsonb jsonb = JsonbBuilder.create(config)) {
            // Act
            String serialized = jsonb.toJson(sut);

            try (JsonReader reader = Json.createReader(new StringReader(serialized))) {
                JsonObject actual = reader.readObject();

                // Assert
                assertThat(actual.keySet())
                        .as("The Organization JSON must contain the field names from the API contract.")
                        .containsExactlyInAnyOrder(
                                "id",
                                "official_address",
                                "annual_turnover",
                                "employees_count",
                                "full_name",
                                "rating",
                                "type",
                                "postal_address"
                        );
            }
        }
    }
}
