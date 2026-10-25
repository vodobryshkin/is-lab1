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
 * Тестовый класс для тестирования класса Worker.
 *
 * @author vodobryshkin
 * @since 16.10.2026 9:44
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class WorkerTest {
    @Test
    void passes_correct_entity() {
        // Arrange
        Worker sut = new Worker(
                1,
                new User(1L, "vodobryshkin", "hash", null),
                "Иван Иванов",
                new Coordinates(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                new Organization(1L, new Address(1L, "official", null), 1f, 1L,
                        "ООО Магистраль", 1, OrganizationType.GOVERNMENT,
                        new Address(2L, "postal", null)),
                1d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Position.DIRECTOR,
                Status.HIRED,
                new Person(1L, Color.BLUE, Color.BROWN, new Location(1L, 0, 0, 0L),
                        ZonedDateTime.parse("2026-10-16T00:00:00+03:00"), 1f, Country.RUSSIA, null)
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
        Worker sut = new Worker(
                1,
                new User(1L, "vodobryshkin", "hash", null),
                "𐄐 ꙮ𝔜̷̵𝔒̶̰ ☲☴ ﷽",
                new Coordinates(1L, -953, -391L),
                ZonedDateTime.parse("2999-01-01T00:00:00+14:00"),
                null,
                0x0.0000000000001P-1022,
                1,
                ZonedDateTime.parse("1900-01-01T00:00:00-12:00"),
                null,
                null,
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
    void passes_correct_entity_with_maximum_numeric_values() {
        // Arrange
        Worker sut = new Worker(
                Integer.MAX_VALUE,
                new User(1L, "vodobryshkin", "hash", null),
                "Иван Иванов",
                new Coordinates(1L, Integer.MAX_VALUE, Long.MAX_VALUE),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                Double.MAX_VALUE,
                Integer.MAX_VALUE,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Position.DIRECTOR,
                Status.HIRED,
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
        Worker sut = new Worker(
                0,
                new User(1L, "vodobryshkin", "hash", null),
                "Иван Иванов",
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

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Worker>> violations =
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
    void rejects_id_which_equals_to_integer_minimum() {
        // Arrange
        Worker sut = new Worker(
                Integer.MIN_VALUE,
                new User(1L, "vodobryshkin", "hash", null),
                "Иван Иванов",
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

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Worker>> violations =
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
    void rejects_name_which_is_null() {
        // Arrange
        Worker sut = new Worker(
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

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Worker>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field name.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field name.")
                            .isEqualTo("name")
            );
        }
    }

    @Test
    void rejects_name_which_is_empty() {
        // Arrange
        Worker sut = new Worker(
                1,
                new User(1L, "vodobryshkin", "hash", null),
                "",
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

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Worker>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field name.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field name.")
                            .isEqualTo("name")
            );
        }
    }

    @Test
    void rejects_coordinates_which_is_null() {
        // Arrange
        Worker sut = new Worker(
                1,
                new User(1L, "vodobryshkin", "hash", null),
                "Иван Иванов",
                null,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Position.DIRECTOR,
                Status.HIRED,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Worker>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field coordinates.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field coordinates.")
                            .isEqualTo("coordinates")
            );
        }
    }

    @Test
    void rejects_creationDate_which_is_null() {
        // Arrange
        Worker sut = new Worker(
                1,
                new User(1L, "vodobryshkin", "hash", null),
                "Иван Иванов",
                new Coordinates(1L, 0, 0L),
                null,
                null,
                1d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Position.DIRECTOR,
                Status.HIRED,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Worker>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field creationDate.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field creationDate.")
                            .isEqualTo("creationDate")
            );
        }
    }

    @Test
    void rejects_salary_which_is_positive_infinity() {
        // Arrange
        Worker sut = new Worker(
                1,
                new User(1L, "vodobryshkin", "hash", null),
                "Иван Иванов",
                new Coordinates(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                Double.POSITIVE_INFINITY,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Position.DIRECTOR,
                Status.HIRED,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Worker>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field salary.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field salary.")
                            .isEqualTo("salary")
            );
        }
    }

    @Test
    void rejects_salary_which_is_nan() {
        // Arrange
        Worker sut = new Worker(
                1,
                new User(1L, "vodobryshkin", "hash", null),
                "Иван Иванов",
                new Coordinates(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                Double.NaN,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Position.DIRECTOR,
                Status.HIRED,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Worker>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field salary.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field salary.")
                            .isEqualTo("salary")
            );
        }
    }

    @Test
    void rejects_salary_which_is_zero() {
        // Arrange
        Worker sut = new Worker(
                1,
                new User(1L, "vodobryshkin", "hash", null),
                "Иван Иванов",
                new Coordinates(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                0d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Position.DIRECTOR,
                Status.HIRED,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Worker>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field salary.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field salary.")
                            .isEqualTo("salary")
            );
        }
    }

    @Test
    void rejects_salary_which_is_negative_infinity() {
        // Arrange
        Worker sut = new Worker(
                1,
                new User(1L, "vodobryshkin", "hash", null),
                "Иван Иванов",
                new Coordinates(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                Double.NEGATIVE_INFINITY,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Position.DIRECTOR,
                Status.HIRED,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Worker>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field salary.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field salary.")
                            .isEqualTo("salary")
            );
        }
    }

    @Test
    void rejects_rating_which_is_null() {
        // Arrange
        Worker sut = new Worker(
                1,
                new User(1L, "vodobryshkin", "hash", null),
                "Иван Иванов",
                new Coordinates(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                null,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Position.DIRECTOR,
                Status.HIRED,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Worker>> violations =
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
        Worker sut = new Worker(
                1,
                new User(1L, "vodobryshkin", "hash", null),
                "Иван Иванов",
                new Coordinates(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                0,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Position.DIRECTOR,
                Status.HIRED,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Worker>> violations =
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
        Worker sut = new Worker(
                1,
                new User(1L, "vodobryshkin", "hash", null),
                "Иван Иванов",
                new Coordinates(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                Integer.MIN_VALUE,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Position.DIRECTOR,
                Status.HIRED,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Worker>> violations =
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
    void rejects_startDate_which_is_null() {
        // Arrange
        Worker sut = new Worker(
                1,
                new User(1L, "vodobryshkin", "hash", null),
                "Иван Иванов",
                new Coordinates(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                1,
                null,
                Position.DIRECTOR,
                Status.HIRED,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Worker>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field startDate.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field startDate.")
                            .isEqualTo("startDate")
            );
        }
    }

    @Test
    void rejects_user_whose_username_is_null() {
        // Arrange
        Worker sut = new Worker(
                1,
                new User(1L, null, "hash", null),
                "Иван Иванов",
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

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Worker>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field user.username.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field user.username.")
                            .isEqualTo("user.username")
            );
        }
    }

    @Test
    void rejects_coordinates_whose_x_is_null() {
        // Arrange
        Worker sut = new Worker(
                1,
                new User(1L, "vodobryshkin", "hash", null),
                "Иван Иванов",
                new Coordinates(1L, null, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Position.DIRECTOR,
                Status.HIRED,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Worker>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field coordinates.x.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field coordinates.x.")
                            .isEqualTo("coordinates.x")
            );
        }
    }

    @Test
    void rejects_organization_whose_fullName_is_null() {
        // Arrange
        Organization organization = new Organization(
                1L,
                new Address(1L, "official", null),
                1f,
                1L,
                null,
                1,
                OrganizationType.GOVERNMENT,
                new Address(2L, "postal", null)
        );

        Worker sut = new Worker(
                1,
                new User(1L, "vodobryshkin", "hash", null),
                "Иван Иванов",
                new Coordinates(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                organization,
                1d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Position.DIRECTOR,
                Status.HIRED,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Worker>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field organization.fullName.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field organization.fullName.")
                            .isEqualTo("organization.fullName")
            );
        }
    }

    @Test
    void rejects_person_whose_hairColor_is_null() {
        // Arrange
        Person person = new Person(
                1L,
                Color.BLUE,
                null,
                new Location(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                1f,
                Country.RUSSIA,
                null
        );

        Worker sut = new Worker(
                1,
                new User(1L, "vodobryshkin", "hash", null),
                "Иван Иванов",
                new Coordinates(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Position.DIRECTOR,
                Status.HIRED,
                person
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<Worker>> violations =
                    factory.getValidator().validate(sut);

            int actualSize = violations.size();
            String actualPropertyName = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .findFirst()
                    .orElse(null);

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("Exactly one violation is expected for field person.hairColor.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("The violation must refer to field person.hairColor.")
                            .isEqualTo("person.hairColor")
            );
        }
    }
}
