package com.github.vodobryshkin.islab1.backend.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Тестовый класс для тестирования класса User.
 *
 * @author vodobryshkin
 * @since 16.10.2026 11:00
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class UserTest {
    @Test
    void passes_correct_entity() {
        // Arrange
        User sut = new User(
                1L,
                "vodobryshkin",
                "hash",
                null
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
    void passes_correct_entity_with_empty_workers() {
        // Arrange
        User sut = new User(
                1L,
                "vodobryshkin",
                "hash",
                List.of()
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
    void passes_correct_entity_with_single_character_username() {
        // Arrange
        User sut = new User(
                1L,
                "я",
                "hash",
                null
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
    void passes_correct_entity_with_username_of_64_characters() {
        // Arrange
        User sut = new User(
                1L,
                "я".repeat(64),
                "hash",
                null
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
    void passes_correct_entity_with_atypical_username() {
        // Arrange
        User sut = new User(
                1L,
                "𐄐 ꙮ𝔜̷̵𝔒̶̰ ☲☴ ﷽",
                "hash",
                null
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
        User sut = new User(
                0L,
                "vodobryshkin",
                "hash",
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<User>> violations =
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
        User sut = new User(
                Long.MIN_VALUE,
                "vodobryshkin",
                "hash",
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<User>> violations =
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
    void rejects_username_which_is_null() {
        // Arrange
        User sut = new User(
                1L,
                null,
                "hash",
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<User>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field username.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field username.")
                            .isEqualTo("username")
            );
        }
    }

    @Test
    void rejects_username_which_is_empty() {
        // Arrange
        User sut = new User(
                1L,
                "",
                "hash",
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<User>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field username.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field username.")
                            .isEqualTo("username")
            );
        }
    }

    @Test
    void rejects_username_which_is_blank() {
        // Arrange
        User sut = new User(
                1L,
                " \t\n\r ",
                "hash",
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<User>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field username.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field username.")
                            .isEqualTo("username")
            );
        }
    }

    @Test
    void rejects_username_which_is_longer_than_64_characters() {
        // Arrange
        User sut = new User(
                1L,
                "я".repeat(65),
                "hash",
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<User>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field username.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field username.")
                            .isEqualTo("username")
            );
        }
    }

    @Test
    void rejects_password_hash_which_is_blank() {
        // Arrange
        User sut = new User(
                1L,
                "vodobryshkin",
                " ",
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<User>> violations =
                    factory.getValidator().validate(sut);

            // Assert
            assertThat(violations)
                    .extracting(violation -> violation.getPropertyPath().toString())
                    .as("The violation must refer to field passwordHash.")
                    .containsExactly("passwordHash");
        }
    }
}
