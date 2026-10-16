package com.github.vodobryshkin.islab1.backend.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Тестовый класс для тестирования класса Organization.
 *
 * @author vodobryshkin
 * @since 16.10.2026 9:00
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class OrganizationTest {
    @Test
    void passes_correct_entity() {
        // Arrange
        Organization sut = new Organization(
                1L,
                new Address(1L, "official", null),
                1f,
                1L,
                "ООО Магистраль",
                1,
                OrganizationType.GOVERNMENT,
                new Address(2L, "postal", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            int actualSize = factory.getValidator().validate(sut).size();

            // Assert
            assertThat(actualSize)
                    .as("Valid entity must not produce constraint violations.")
                    .isEqualTo(0);
        }
    }

    @Test
    void passes_correct_entity_with_atypical_values() {
        // Arrange
        Organization sut = new Organization(
                1L,
                new Address(1L, "☣ official ꙮ", new Location(1L, -1, 0, Long.MIN_VALUE)),
                0x0.000002P-126f,
                1L,
                "𐄐 ꙮ𝔜̷̵𝔒̶̰ ☲☴ ﷽",
                1,
                null,
                new Address(2L, "☠ postal ☲", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            int actualSize = factory.getValidator().validate(sut).size();

            // Assert
            assertThat(actualSize)
                    .as("Valid entity must not produce constraint violations.")
                    .isEqualTo(0);
        }
    }

    @Test
    void passes_correct_entity_with_maximum_numeric_values() {
        // Arrange
        Organization sut = new Organization(
                1L,
                new Address(1L, "official", null),
                Float.MAX_VALUE,
                Long.MAX_VALUE,
                "ООО Магистраль",
                Integer.MAX_VALUE,
                OrganizationType.GOVERNMENT,
                new Address(2L, "postal", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            int actualSize = factory.getValidator().validate(sut).size();

            // Assert
            assertThat(actualSize)
                    .as("Valid entity must not produce constraint violations.")
                    .isEqualTo(0);
        }
    }

    @Test
    void passes_correct_entity_with_long_fullName() {
        // Arrange
        Organization sut = new Organization(
                1L,
                new Address(1L, "official", null),
                1f,
                1L,
                "ꙮ".repeat(1_024),
                1,
                OrganizationType.GOVERNMENT,
                new Address(2L, "postal", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            int actualSize = factory.getValidator().validate(sut).size();

            // Assert
            assertThat(actualSize)
                    .as("Valid entity must not produce constraint violations.")
                    .isEqualTo(0);
        }
    }

    @Test
    void rejects_id_which_is_zero() {
        // Arrange
        Organization sut = new Organization(
                0L,
                new Address(1L, "official", null),
                1f,
                1L,
                "ООО Магистраль",
                1,
                OrganizationType.GOVERNMENT,
                new Address(2L, "postal", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Organization>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field id.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field id.")
                            .isEqualTo("id")
            );
        }
    }

    @Test
    void rejects_id_which_equals_to_long_minimum() {
        // Arrange
        Organization sut = new Organization(
                Long.MIN_VALUE,
                new Address(1L, "official", null),
                1f,
                1L,
                "ООО Магистраль",
                1,
                OrganizationType.GOVERNMENT,
                new Address(2L, "postal", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Organization>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field id.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field id.")
                            .isEqualTo("id")
            );
        }
    }

    @Test
    void rejects_officialAddress_which_is_null() {
        // Arrange
        Organization sut = new Organization(
                1L,
                null,
                1f,
                1L,
                "ООО Магистраль",
                1,
                OrganizationType.GOVERNMENT,
                new Address(2L, "postal", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Organization>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field officialAddress.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field officialAddress.")
                            .isEqualTo("officialAddress")
            );
        }
    }

    @Test
    void rejects_annualTurnover_which_is_positive_infinity() {
        // Arrange
        Organization sut = new Organization(
                1L,
                new Address(1L, "official", null),
                Float.POSITIVE_INFINITY,
                1L,
                "ООО Магистраль",
                1,
                OrganizationType.GOVERNMENT,
                new Address(2L, "postal", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Organization>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field annualTurnover.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field annualTurnover.")
                            .isEqualTo("annualTurnover")
            );
        }
    }

    @Test
    void rejects_annualTurnover_which_is_nan() {
        // Arrange
        Organization sut = new Organization(
                1L,
                new Address(1L, "official", null),
                Float.NaN,
                1L,
                "ООО Магистраль",
                1,
                OrganizationType.GOVERNMENT,
                new Address(2L, "postal", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Organization>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field annualTurnover.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field annualTurnover.")
                            .isEqualTo("annualTurnover")
            );
        }
    }

    @Test
    void rejects_annualTurnover_which_is_zero() {
        // Arrange
        Organization sut = new Organization(
                1L,
                new Address(1L, "official", null),
                0f,
                1L,
                "ООО Магистраль",
                1,
                OrganizationType.GOVERNMENT,
                new Address(2L, "postal", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Organization>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field annualTurnover.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field annualTurnover.")
                            .isEqualTo("annualTurnover")
            );
        }
    }

    @Test
    void rejects_annualTurnover_which_is_negative_infinity() {
        // Arrange
        Organization sut = new Organization(
                1L,
                new Address(1L, "official", null),
                Float.NEGATIVE_INFINITY,
                1L,
                "ООО Магистраль",
                1,
                OrganizationType.GOVERNMENT,
                new Address(2L, "postal", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Organization>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field annualTurnover.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field annualTurnover.")
                            .isEqualTo("annualTurnover")
            );
        }
    }

    @Test
    void rejects_employeesCount_which_is_null() {
        // Arrange
        Organization sut = new Organization(
                1L,
                new Address(1L, "official", null),
                1f,
                null,
                "ООО Магистраль",
                1,
                OrganizationType.GOVERNMENT,
                new Address(2L, "postal", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Organization>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field employeesCount.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field employeesCount.")
                            .isEqualTo("employeesCount")
            );
        }
    }

    @Test
    void rejects_employeesCount_which_is_zero() {
        // Arrange
        Organization sut = new Organization(
                1L,
                new Address(1L, "official", null),
                1f,
                0L,
                "ООО Магистраль",
                1,
                OrganizationType.GOVERNMENT,
                new Address(2L, "postal", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Organization>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field employeesCount.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field employeesCount.")
                            .isEqualTo("employeesCount")
            );
        }
    }

    @Test
    void rejects_employeesCount_which_equals_to_long_minimum() {
        // Arrange
        Organization sut = new Organization(
                1L,
                new Address(1L, "official", null),
                1f,
                Long.MIN_VALUE,
                "ООО Магистраль",
                1,
                OrganizationType.GOVERNMENT,
                new Address(2L, "postal", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Organization>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field employeesCount.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field employeesCount.")
                            .isEqualTo("employeesCount")
            );
        }
    }

    @Test
    void rejects_fullName_which_is_null() {
        // Arrange
        Organization sut = new Organization(
                1L,
                new Address(1L, "official", null),
                1f,
                1L,
                null,
                1,
                OrganizationType.GOVERNMENT,
                new Address(2L, "postal", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Organization>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field fullName.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field fullName.")
                            .isEqualTo("fullName")
            );
        }
    }

    @Test
    void rejects_fullName_which_is_empty() {
        // Arrange
        Organization sut = new Organization(
                1L,
                new Address(1L, "official", null),
                1f,
                1L,
                "",
                1,
                OrganizationType.GOVERNMENT,
                new Address(2L, "postal", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Organization>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field fullName.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field fullName.")
                            .isEqualTo("fullName")
            );
        }
    }

    @Test
    void rejects_rating_which_is_null() {
        // Arrange
        Organization sut = new Organization(
                1L,
                new Address(1L, "official", null),
                1f,
                1L,
                "ООО Магистраль",
                null,
                OrganizationType.GOVERNMENT,
                new Address(2L, "postal", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Organization>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field rating.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field rating.")
                            .isEqualTo("rating")
            );
        }
    }

    @Test
    void rejects_rating_which_is_zero() {
        // Arrange
        Organization sut = new Organization(
                1L,
                new Address(1L, "official", null),
                1f,
                1L,
                "ООО Магистраль",
                0,
                OrganizationType.GOVERNMENT,
                new Address(2L, "postal", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Organization>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field rating.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field rating.")
                            .isEqualTo("rating")
            );
        }
    }

    @Test
    void rejects_rating_which_equals_to_integer_minimum() {
        // Arrange
        Organization sut = new Organization(
                1L,
                new Address(1L, "official", null),
                1f,
                1L,
                "ООО Магистраль",
                Integer.MIN_VALUE,
                OrganizationType.GOVERNMENT,
                new Address(2L, "postal", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Organization>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field rating.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field rating.")
                            .isEqualTo("rating")
            );
        }
    }

    @Test
    void rejects_postalAddress_which_is_null() {
        // Arrange
        Organization sut = new Organization(
                1L,
                new Address(1L, "official", null),
                1f,
                1L,
                "ООО Магистраль",
                1,
                OrganizationType.GOVERNMENT,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Organization>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field postalAddress.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field postalAddress.")
                            .isEqualTo("postalAddress")
            );
        }
    }

    @Test
    void rejects_officialAddress_whose_street_is_null() {
        // Arrange
        Organization sut = new Organization(
                1L,
                new Address(1L, null, null),
                1f,
                1L,
                "ООО Магистраль",
                1,
                OrganizationType.GOVERNMENT,
                new Address(2L, "postal", null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Organization>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field officialAddress.street.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field officialAddress.street.")
                            .isEqualTo("officialAddress.street")
            );
        }
    }

    @Test
    void rejects_postalAddress_whose_street_is_null() {
        // Arrange
        Organization sut = new Organization(
                1L,
                new Address(1L, "official", null),
                1f,
                1L,
                "ООО Магистраль",
                1,
                OrganizationType.GOVERNMENT,
                new Address(2L, null, null)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Organization>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field postalAddress.street.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field postalAddress.street.")
                            .isEqualTo("postalAddress.street")
            );
        }
    }
}
