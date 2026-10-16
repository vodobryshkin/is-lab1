package com.github.vodobryshkin.islab1.backend.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Тестовый класс для тестирования класса Location.
 *
 * @author vodobryshkin
 * @since 16.10.2026 8:34
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class LocationTest {
    @Test
    void passes_correct_entity() {
        // Arrange
        Location sut = new Location(1L, 1, 1, 0L);

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
        Location sut = new Location(0L, 1, 1, 0L);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Location>> violations =
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
        Location sut = new Location(Long.MIN_VALUE, 1, 1, 0L);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Location>> violations =
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
    void rejects_x_coordinate_which_is_null() {
        // Arrange
        Location sut = new Location(1L, null, 1, 0L);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Location>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field x.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field x.")
                            .isEqualTo("x")
            );
        }
    }

    @Test
    void rejects_y_coordinate_which_is_null() {
        // Arrange
        Location sut = new Location(1L, 1, null, 0L);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Location>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field y.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field y.")
                            .isEqualTo("y")
            );
        }
    }

    @Test
    void rejects_z_coordinate_which_is_null() {
        // Arrange
        Location sut = new Location(1L, 1, 1, null);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Location>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field z.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field z.")
                            .isEqualTo("z")
            );
        }
    }
}
