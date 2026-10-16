package com.github.vodobryshkin.islab1.backend.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.github.vodobryshkin.islab1.backend.domain.Color;
import com.github.vodobryshkin.islab1.backend.domain.Country;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.io.StringReader;
import java.time.ZonedDateTime;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Тестовый класс для тестирования класса PersonResponse.
 *
 * @author vodobryshkin
 * @since 16.10.2026 00:31
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class PersonResponseTest {
    @Test
    void passes_correct_dto() {
        PersonResponse sut = new PersonResponse(
                1L,
                Color.BLUE,
                Color.BROWN,
                new LocationResponse(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                1f,
                Country.RUSSIA
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            int actualSize = factory.getValidator().validate(sut).size();

            assertThat(actualSize)
                    .as(
                            "If there are no errors in the DTO, then violations should not be thrown "
                                    + "out."
                    )
                    .isEqualTo(0);
        }
    }

    @Test
    void rejects_id_which_is_null() {
        // Arrange
        PersonResponse sut = new PersonResponse(
                null,
                Color.BLUE,
                Color.BROWN,
                new LocationResponse(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                1f,
                Country.RUSSIA
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<PersonResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<PersonResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as(
                                    "If id is invalid and all other fields are valid, one violation is "
                                            + "expected."
                            )
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
        PersonResponse sut = new PersonResponse(
                0L,
                Color.BLUE,
                Color.BROWN,
                new LocationResponse(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                1f,
                Country.RUSSIA
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<PersonResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<PersonResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as(
                                    "If id is invalid and all other fields are valid, one violation is "
                                            + "expected."
                            )
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
        PersonResponse sut = new PersonResponse(
                Long.MIN_VALUE,
                Color.BLUE,
                Color.BROWN,
                new LocationResponse(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                1f,
                Country.RUSSIA
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<PersonResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<PersonResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as(
                                    "If id is invalid and all other fields are valid, one violation is "
                                            + "expected."
                            )
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field id equal to Long.MIN_VALUE must not be validated.")
                            .isEqualTo("id")
            );
        }
    }

    @Test
    void rejects_hairColor_which_is_null() {
        // Arrange
        PersonResponse sut = new PersonResponse(
                1L,
                Color.BLUE,
                null,
                new LocationResponse(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                1f,
                Country.RUSSIA
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<PersonResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<PersonResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as(
                                    "If hairColor is invalid and all other fields are valid, one violation is "
                                            + "expected."
                            )
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field hairColor which is null must not be validated.")
                            .isEqualTo("hairColor")
            );
        }
    }

    @Test
    void rejects_location_which_is_null() {
        // Arrange
        PersonResponse sut = new PersonResponse(
                1L,
                Color.BLUE,
                Color.BROWN,
                null,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                1f,
                Country.RUSSIA
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<PersonResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<PersonResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as(
                                    "If location is invalid and all other fields are valid, one violation is "
                                            + "expected."
                            )
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field location which is null must not be validated.")
                            .isEqualTo("location")
            );
        }
    }

    @Test
    void validates_incorrect_location_cascade() {
        // Arrange
        PersonResponse sut = new PersonResponse(
                1L,
                Color.BLUE,
                Color.BROWN,
                new LocationResponse(null, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                1f,
                Country.RUSSIA
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<PersonResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<PersonResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as(
                                    "If location.id is invalid and all other fields are valid, one violation is "
                                            + "expected."
                            )
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field location.id which is null must not be validated.")
                            .isEqualTo("location.id")
            );
        }
    }

    @Test
    void rejects_birthday_which_is_null() {
        // Arrange
        PersonResponse sut = new PersonResponse(
                1L,
                Color.BLUE,
                Color.BROWN,
                new LocationResponse(1L, 1, 1, 1L),
                null,
                1f,
                Country.RUSSIA
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<PersonResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<PersonResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as(
                                    "If birthday is invalid and all other fields are valid, one violation is "
                                            + "expected."
                            )
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field birthday which is null must not be validated.")
                            .isEqualTo("birthday")
            );
        }
    }

    @Test
    void rejects_weight_which_is_nan() {
        // Arrange
        PersonResponse sut = new PersonResponse(
                1L,
                Color.BLUE,
                Color.BROWN,
                new LocationResponse(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Float.NaN,
                Country.RUSSIA
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<PersonResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<PersonResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as(
                                    "If weight is invalid and all other fields are valid, one violation is "
                                            + "expected."
                            )
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field weight which is NaN must not be validated.")
                            .isEqualTo("weight")
            );
        }
    }

    @Test
    void rejects_weight_below_minimum() {
        // Arrange
        PersonResponse sut = new PersonResponse(
                1L,
                Color.BLUE,
                Color.BROWN,
                new LocationResponse(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                0f,
                Country.RUSSIA
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<PersonResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<PersonResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as(
                                    "If weight is invalid and all other fields are valid, one violation is "
                                            + "expected."
                            )
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field weight which is not positive must not be validated.")
                            .isEqualTo("weight")
            );
        }
    }

    @Test
    void rejects_weight_which_equals_to_float_minimum() {
        // Arrange
        PersonResponse sut = new PersonResponse(
                1L,
                Color.BLUE,
                Color.BROWN,
                new LocationResponse(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                -Float.MIN_VALUE,
                Country.RUSSIA
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<PersonResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<PersonResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as(
                                    "If weight is invalid and all other fields are valid, one violation is "
                                            + "expected."
                            )
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field weight which is not positive must not be validated.")
                            .isEqualTo("weight")
            );
        }
    }

    @Test
    void rejects_nationality_which_is_null() {
        // Arrange
        PersonResponse sut = new PersonResponse(
                1L,
                Color.BLUE,
                Color.BROWN,
                new LocationResponse(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                1f,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<PersonResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<PersonResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as(
                                    "If nationality is invalid and all other fields are valid, one violation is "
                                            + "expected."
                            )
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field nationality which is null must not be validated.")
                            .isEqualTo("nationality")
            );
        }
    }

    @Test
    void serializes_person_with_expected_property_names() throws Exception {
        // Arrange
        PersonResponse sut = new PersonResponse(
                1L,
                Color.BLUE,
                Color.BROWN,
                new LocationResponse(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                1f,
                Country.RUSSIA
        );
        JsonbConfig config = new JsonbConfig().withNullValues(true);

        try (Jsonb jsonb = JsonbBuilder.create(config)) {
            // Act
            String serialized = jsonb.toJson(sut);

            try (JsonReader reader = Json.createReader(new StringReader(serialized))) {
                JsonObject actual = reader.readObject();

                // Assert
                assertThat(actual.keySet())
                        .as(
                                "The Person JSON must contain the field names from the API contract."
                        )
                        .containsExactlyInAnyOrder(
                                "id",
                                "eye_color",
                                "hair_color",
                                "location",
                                "birthday",
                                "weight",
                                "nationality"
                        );
            }
        }
    }

    @Test
    void rejects_weight_which_is_positive_infinity() {
        // Arrange
        PersonResponse sut = new PersonResponse(
                1L,
                Color.BLUE,
                Color.BROWN,
                new LocationResponse(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Float.POSITIVE_INFINITY,
                Country.RUSSIA
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<PersonResponse>> violations =
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
        PersonResponse sut = new PersonResponse(
                1L,
                Color.BLUE,
                Color.BROWN,
                new LocationResponse(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Float.NEGATIVE_INFINITY,
                Country.RUSSIA
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<PersonResponse>> violations =
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
}
