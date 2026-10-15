package com.github.vodobryshkin.islab1.backend.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * Тестовый класс для тестирования класса AddressResponse.
 *
 * @author vodobryshkin
 * @since 15.10.2026 22:08
 */
public class AddressResponseTest {
    @Test
    void passes_correct_DTO() {
        AddressResponse sut = new AddressResponse(
                1L,
                "Улица Пушкина, дом Колотушкина 1",
                new LocationResponse(1L, 1, 1, 0L)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            int actualSize  = factory.getValidator().validate(sut).size();

            assertThat(actualSize)
                    .as("If there are no errors in the DTO, then violations should not be thrown out.")
                    .isEqualTo(0);
        }
    }

    @Test
    void passes_correct_DTO_with_atypical_street() {
        AddressResponse sut = new AddressResponse(
                1L,
                "\uD80C\uDC10\uD80C\uDCB8 ꙮ\uD835\uDD1C̷̵\uD835\uDD12̶̰\uD835\uDD18̵̕" +
                        "\uD80C\uDD3F\uD80C\uDD5C ☲☴ \uD869\uDEA5  \uD86D\uDF7C " +
                        "\uD808\uDD99\uD808\uDE14\uD808\uDD52﷽\u06DD ҂҂ ⃢⃟⃘ ꩵꩶ",
                new LocationResponse(1L, 1, 1, 0L)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            int actualSize  = factory.getValidator().validate(sut).size();

            assertThat(actualSize)
                    .as("If there are no errors in the DTO, then violations should not be thrown out.")
                    .isEqualTo(0);
        }
    }

    @Test
    void passes_correct_DTO_when_town_is_null() {
        AddressResponse sut = new AddressResponse(
                1L,
                "Улица Пушкина, дом Колотушкина 1",
                null
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
        AddressResponse sut = new AddressResponse(
                null,
                "Улица Пушкина, дом Колотушкина",
                new LocationResponse(1L, 1, 1, 0L)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<AddressResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<AddressResponse> violation = violations.iterator().next();

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
        Long forbiddenId = 0L;
        AddressResponse sut = new AddressResponse(
                forbiddenId,
                "Улица Пушкина, дом Колотушкина 1",
                new LocationResponse(1L, 1, 1, 0L)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<AddressResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<AddressResponse> violation = violations.iterator().next();

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
        Long forbiddenId = Long.MIN_VALUE;
        AddressResponse sut = new AddressResponse(
                forbiddenId,
                "Улица Пушкина, дом Колотушкина 1",
                new LocationResponse(1L, 1, 1, 0L)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<AddressResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<AddressResponse> violation = violations.iterator().next();

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
    void rejects_street_which_is_empty() {
        // Arrange
        String forbiddenStreet = "";
        AddressResponse sut = new AddressResponse(
                1L,
                forbiddenStreet,
                new LocationResponse(1L, 1, 1, 0L)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<AddressResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<AddressResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If street is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Field street which is empty must not be validated.")
                            .isEqualTo("street")
            );
        }
    }

    @Test
    void rejects_street_which_is_null() {
        // Arrange
        AddressResponse sut = new AddressResponse(
                1L,
                null,
                new LocationResponse(1L, 1, 1, 0L)
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<AddressResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<AddressResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If street is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Field street which is equal to null must not be validated.")
                            .isEqualTo("street")
            );
        }
    }

    @Test
    void validates_incorrect_town_cascade() {
        // Arrange
        LocationResponse incorrectTown = new LocationResponse(null, 1, 1, 0L);
        AddressResponse sut = new AddressResponse(
                1L,
                "Улица Пушкина, дом Колотушкина 1",
                incorrectTown
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<AddressResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<AddressResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If town is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field id in town which is equal to null must not be validated.")
                            .isEqualTo("town.id")
            );
        }
    }

    @Test
    void gives_as_many_fields_as_incorrect_in_town() {
        // Arrange
        LocationResponse incorrectTown = new LocationResponse(0L, 1, 1, null);
        AddressResponse sut = new AddressResponse(
                1L,
                "Улица Пушкина, дом Колотушкина 1",
                incorrectTown
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<AddressResponse>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();

            // Assert
            assertThat(actualSize)
                    .as("Amount of violations to return must be equal to number of violations in town.")
                    .isEqualTo(2);
        }
    }
}