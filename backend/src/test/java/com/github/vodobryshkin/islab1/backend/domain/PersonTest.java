package com.github.vodobryshkin.islab1.backend.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.time.ZonedDateTime;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Тестовый класс для тестирования класса Person.
 *
 * @author vodobryshkin
 * @since 16.10.2026 10:12
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class PersonTest {
    @Test
    void passes_correct_entity() {
        // Arrange
        Person sut = new Person(
                1L,
                Color.BLUE,
                Color.BROWN,
                new Location(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                1f,
                Country.RUSSIA,
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
        Person sut = new Person(
                0L,
                Color.BLUE,
                Color.BROWN,
                new Location(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                1f,
                Country.RUSSIA,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Person>> violations =
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
        Person sut = new Person(
                Long.MIN_VALUE,
                Color.BLUE,
                Color.BROWN,
                new Location(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                1f,
                Country.RUSSIA,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Person>> violations =
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
    void passes_correct_entity_with_atypical_values() {
        // Arrange
        Person sut = new Person(
                1L,
                null,
                Color.ORANGE,
                new Location(1L, Integer.MIN_VALUE, Integer.MAX_VALUE, Long.MIN_VALUE),
                ZonedDateTime.parse("2999-01-01T00:00:00+14:00"),
                Float.MIN_VALUE,
                Country.UNITED_KINGDOM,
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
    void passes_correct_entity_with_maximum_weight() {
        // Arrange
        Person sut = new Person(
                1L,
                Color.BLUE,
                Color.BROWN,
                new Location(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Float.MAX_VALUE,
                Country.RUSSIA,
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
    void rejects_hairColor_which_is_null() {
        // Arrange
        Person sut = new Person(
                1L,
                Color.BLUE,
                null,
                new Location(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                1f,
                Country.RUSSIA,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Person>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field hairColor.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field hairColor.")
                            .isEqualTo("hairColor")
            );
        }
    }

    @Test
    void rejects_location_which_is_null() {
        // Arrange
        Person sut = new Person(
                1L,
                Color.BLUE,
                Color.BROWN,
                null,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                1f,
                Country.RUSSIA,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Person>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field location.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field location.")
                            .isEqualTo("location")
            );
        }
    }

    @Test
    void rejects_birthday_which_is_null() {
        // Arrange
        Person sut = new Person(
                1L,
                Color.BLUE,
                Color.BROWN,
                new Location(1L, 1, 1, 1L),
                null,
                1f,
                Country.RUSSIA,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Person>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field birthday.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field birthday.")
                            .isEqualTo("birthday")
            );
        }
    }

    @Test
    void rejects_weight_which_is_null() {
        // Arrange
        Person sut = new Person(
                1L,
                Color.BLUE,
                Color.BROWN,
                new Location(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                Country.RUSSIA,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Person>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field weight.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field weight.")
                            .isEqualTo("weight")
            );
        }
    }

    @Test
    void rejects_weight_which_is_positive_infinity() {
        // Arrange
        Person sut = new Person(
                1L,
                Color.BLUE,
                Color.BROWN,
                new Location(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Float.POSITIVE_INFINITY,
                Country.RUSSIA,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Person>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field weight.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field weight.")
                            .isEqualTo("weight")
            );
        }
    }

    @Test
    void rejects_weight_which_is_nan() {
        // Arrange
        Person sut = new Person(
                1L,
                Color.BLUE,
                Color.BROWN,
                new Location(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Float.NaN,
                Country.RUSSIA,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Person>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field weight.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field weight.")
                            .isEqualTo("weight")
            );
        }
    }

    @Test
    void rejects_weight_which_is_zero() {
        // Arrange
        Person sut = new Person(
                1L,
                Color.BLUE,
                Color.BROWN,
                new Location(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                0f,
                Country.RUSSIA,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Person>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field weight.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field weight.")
                            .isEqualTo("weight")
            );
        }
    }

    @Test
    void rejects_weight_which_is_negative_infinity() {
        // Arrange
        Person sut = new Person(
                1L,
                Color.BLUE,
                Color.BROWN,
                new Location(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Float.NEGATIVE_INFINITY,
                Country.RUSSIA,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Person>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field weight.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field weight.")
                            .isEqualTo("weight")
            );
        }
    }

    @Test
    void rejects_nationality_which_is_null() {
        // Arrange
        Person sut = new Person(
                1L,
                Color.BLUE,
                Color.BROWN,
                new Location(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                1f,
                null,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Person>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field nationality.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field nationality.")
                            .isEqualTo("nationality")
            );
        }
    }

    @Test
    void rejects_location_whose_x_is_null() {
        // Arrange
        Person sut = new Person(
                1L,
                Color.BLUE,
                Color.BROWN,
                new Location(1L, null, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                1f,
                Country.RUSSIA,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Person>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field location.x.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field location.x.")
                            .isEqualTo("location.x")
            );
        }
    }

    @Test
    void rejects_worker_whose_name_is_null() {
        // Arrange
        Worker worker = new Worker(
                1,
                new User(1L, "vodobryshkin", "hash", null),
                null,
                new Coordinates(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Position.DIRECTOR,
                Status.HIRED,
                null
        );

        Person sut = new Person(
                1L,
                Color.BLUE,
                Color.BROWN,
                new Location(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                1f,
                Country.RUSSIA,
                worker
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Person>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field worker.name.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field worker.name.")
                            .isEqualTo("worker.name")
            );
        }
    }
}
