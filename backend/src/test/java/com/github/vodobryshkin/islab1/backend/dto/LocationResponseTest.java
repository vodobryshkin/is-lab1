package com.github.vodobryshkin.islab1.backend.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * Тестовый класс для тестирования класса LocationResponse.
 * 
 * @author vodobryshkin
 * @since 15.10.2026 21:54
 */
public class LocationResponseTest {
    @Test
    void passes_correct_DTO() {
        LocationResponse sut = new LocationResponse(1L, 1, 1, 0L);

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
        LocationResponse sut = new LocationResponse(null, 1, 1, 0L);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<LocationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<LocationResponse> violation = violations.iterator().next();

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
        LocationResponse sut = new LocationResponse(forbiddenId, 1, 1, 0L);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<LocationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<LocationResponse> violation = violations.iterator().next();

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
        LocationResponse sut = new LocationResponse(forbiddenId, 1, 1, 0L);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<LocationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<LocationResponse> violation = violations.iterator().next();

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
    void rejects_x_which_is_null() {
        // Arrange
        LocationResponse sut = new LocationResponse(1L, null, 1, 0L);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<LocationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<LocationResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If x is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Field x which is null must not be validated.")
                            .isEqualTo("x")
            );
        }
    }

    @Test
    void rejects_y_which_is_null() {
        // Arrange
        LocationResponse sut = new LocationResponse(1L, 1, null, 0L);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<LocationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<LocationResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If y is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Field y which is null must not be validated.")
                            .isEqualTo("y")
            );
        }
    }

    @Test
    void rejects_z_which_is_null() {
        // Arrange
        LocationResponse sut = new LocationResponse(1L, 1, 1, null);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<LocationResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<LocationResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If z is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Field id which is null must not be validated.")
                            .isEqualTo("z")
            );
        }
    }
}
