package com.github.vodobryshkin.islab1.backend.dto;

import com.github.vodobryshkin.islab1.backend.domain.Position;
import com.github.vodobryshkin.islab1.backend.domain.Status;
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
import java.time.ZonedDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * Тестовый класс для тестирования класса WorkerResponse.
 *
 * @author vodobryshkin
 * @since 16.10.2026 00:42
 */
public class WorkerResponseTest {
    @Test
    void passes_correct_DTO() {
        // Arrange
        WorkerResponse sut = new WorkerResponse(
                1,
                "Иван Иванов",
                new CoordinatesResponse(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                Position.DIRECTOR,
                Status.HIRED,
                null,
                1L
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            int actualSize = factory.getValidator().validate(sut).size();

            // Assert
            assertThat(actualSize)
                    .as("If there are no errors in the DTO, then violations should not be thrown out.")
                    .isEqualTo(0);
        }
    }

    @Test
    void passes_correct_DTO_with_atypical_name() {
        // Arrange
        WorkerResponse sut = new WorkerResponse(
                1,
                "\uD835\uDD08\uD835\uDD2F\uD835\uDD2F\uD835\uDD2C\uD835\uDD2F ☣ ☠",
                new CoordinatesResponse(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                null,
                null,
                1L
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            int actualSize = factory.getValidator().validate(sut).size();

            // Assert
            assertThat(actualSize)
                    .as("If name contains atypical symbols and all other fields are valid, violations should not be thrown out.")
                    .isEqualTo(0);
        }
    }

    @Test
    void passes_DTO_with_maximum_numeric_values() {
        // Arrange
        WorkerResponse sut = new WorkerResponse(
                Integer.MAX_VALUE,
                "Иван Иванов",
                new CoordinatesResponse(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                Double.MAX_VALUE,
                Integer.MAX_VALUE,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                null,
                null,
                Long.MAX_VALUE
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            int actualSize = factory.getValidator().validate(sut).size();

            // Assert
            assertThat(actualSize)
                    .as("Maximum values of numeric fields must satisfy positive-value constraints.")
                    .isEqualTo(0);
        }
    }

    @Test
    void rejects_id_which_is_null() {
        // Arrange
        WorkerResponse sut = new WorkerResponse(
                null,
                "Иван Иванов",
                new CoordinatesResponse(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                null,
                null,
                1L
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<WorkerResponse>> violations =
                    factory.getValidator().validate(sut);
            ConstraintViolation<WorkerResponse> violation = violations.iterator().next();
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
        WorkerResponse sut = new WorkerResponse(
                0,
                "Иван Иванов",
                new CoordinatesResponse(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                null,
                null,
                1L
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<WorkerResponse>> violations =
                    factory.getValidator().validate(sut);
            ConstraintViolation<WorkerResponse> violation = violations.iterator().next();
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
    void rejects_id_which_equals_to_integer_minimum() {
        // Arrange
        WorkerResponse sut = new WorkerResponse(
                Integer.MIN_VALUE,
                "Иван Иванов",
                new CoordinatesResponse(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                null,
                null,
                1L
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<WorkerResponse>> violations =
                    factory.getValidator().validate(sut);
            ConstraintViolation<WorkerResponse> violation = violations.iterator().next();
            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If id is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field id equal to Integer.MIN_VALUE must not be validated.")
                            .isEqualTo("id")
            );
        }
    }

    @Test
    void rejects_name_which_is_null() {
        // Arrange
        WorkerResponse sut = new WorkerResponse(
                1,
                null,
                new CoordinatesResponse(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                null,
                null,
                1L
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<WorkerResponse>> violations = factory.getValidator().validate(sut);
            ConstraintViolation<WorkerResponse> violation = violations.iterator().next();
            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If name is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field name which is null must not be validated.")
                            .isEqualTo("name")
            );
        }
    }

    @Test
    void rejects_name_which_is_empty() {
        // Arrange
        WorkerResponse sut = new WorkerResponse(
                1,
                "",
                new CoordinatesResponse(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                null,
                null,
                1L
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<WorkerResponse>> violations = factory.getValidator().validate(sut);
            ConstraintViolation<WorkerResponse> violation = violations.iterator().next();
            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If name is invalid and all other fields are valid, at least one violation is expected.")
                            .isGreaterThanOrEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field name which is empty must not be validated.")
                            .isEqualTo("name")
            );
        }
    }

    @Test
    void rejects_coordinates_which_is_null() {
        // Arrange
        WorkerResponse sut = new WorkerResponse(
                1,
                "Иван Иванов",
                null,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                null,
                null,
                1L
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<WorkerResponse>> violations = factory.getValidator().validate(sut);
            ConstraintViolation<WorkerResponse> violation = violations.iterator().next();
            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If coordinates is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field coordinates which is null must not be validated.")
                            .isEqualTo("coordinates")
            );
        }
    }

    @Test
    void rejects_creationDate_which_is_null() {
        // Arrange
        WorkerResponse sut = new WorkerResponse(
                1,
                "Иван Иванов",
                new CoordinatesResponse(1L, 0, 0L),
                null,
                null,
                1d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                null,
                null,
                1L
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<WorkerResponse>> violations = factory.getValidator().validate(sut);
            ConstraintViolation<WorkerResponse> violation = violations.iterator().next();
            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If creationDate is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field creationDate which is null must not be validated.")
                            .isEqualTo("creationDate")
            );
        }
    }

    @Test
    void rejects_salary_which_is_NaN() {
        // Arrange
        WorkerResponse sut = new WorkerResponse(
                1,
                "Иван Иванов",
                new CoordinatesResponse(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                Double.NaN,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                null,
                null,
                1L
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<WorkerResponse>> violations = factory.getValidator().validate(sut);
            ConstraintViolation<WorkerResponse> violation = violations.iterator().next();
            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If salary is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field salary which is NaN must not be validated.")
                            .isEqualTo("salary")
            );
        }
    }

    @Test
    void rejects_salary_below_minimum() {
        // Arrange
        WorkerResponse sut = new WorkerResponse(
                1,
                "Иван Иванов",
                new CoordinatesResponse(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                0d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                null,
                null,
                1L
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<WorkerResponse>> violations = factory.getValidator().validate(sut);
            ConstraintViolation<WorkerResponse> violation = violations.iterator().next();
            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If salary is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field salary which is not positive must not be validated.")
                            .isEqualTo("salary")
            );
        }
    }

    @Test
    void rejects_salary_which_equals_to_double_minimum() {
        // Arrange
        WorkerResponse sut = new WorkerResponse(
                1,
                "Иван Иванов",
                new CoordinatesResponse(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                -Double.MIN_VALUE,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                null,
                null,
                1L
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<WorkerResponse>> violations = factory.getValidator().validate(sut);
            ConstraintViolation<WorkerResponse> violation = violations.iterator().next();
            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If salary is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field salary equal to -Double.MIN_VALUE must not be validated.")
                            .isEqualTo("salary")
            );
        }
    }

    @Test
    void rejects_rating_below_minimum() {
        // Arrange
        WorkerResponse sut = new WorkerResponse(
                1,
                "Иван Иванов",
                new CoordinatesResponse(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                0,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                null,
                null,
                1L
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<WorkerResponse>> violations = factory.getValidator().validate(sut);
            ConstraintViolation<WorkerResponse> violation = violations.iterator().next();
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
        WorkerResponse sut = new WorkerResponse(
                1,
                "Иван Иванов",
                new CoordinatesResponse(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                Integer.MIN_VALUE,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                null,
                null,
                1L
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<WorkerResponse>> violations = factory.getValidator().validate(sut);
            ConstraintViolation<WorkerResponse> violation = violations.iterator().next();
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
    void rejects_startDate_which_is_null() {
        // Arrange
        WorkerResponse sut = new WorkerResponse(
                1,
                "Иван Иванов",
                new CoordinatesResponse(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                1,
                null,
                null,
                null,
                null,
                1L
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<WorkerResponse>> violations = factory.getValidator().validate(sut);
            ConstraintViolation<WorkerResponse> violation = violations.iterator().next();
            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If startDate is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field startDate which is null must not be validated.")
                            .isEqualTo("startDate")
            );
        }
    }

    @Test
    void validates_incorrect_organization_cascade() {
        // Arrange
        OrganizationResponse organization = new OrganizationResponse(
                2L,
                new AddressResponse(1L, "official", null),
                1f,
                1L,
                null,
                1,
                null,
                new AddressResponse(1L, "postal", null)
        );
        WorkerResponse sut = new WorkerResponse(
                1,
                "Иван Иванов",
                new CoordinatesResponse(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                organization,
                1d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                null,
                null,
                1L
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<WorkerResponse>> violations = factory.getValidator().validate(sut);
            ConstraintViolation<WorkerResponse> violation = violations.iterator().next();
            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If organization.fullName is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field organization.fullName must be validated with cascading validation.")
                            .isEqualTo("organization.fullName")
            );
        }
    }

    @Test
    void validates_incorrect_person_cascade() {
        // Arrange
        PersonResponse person = new PersonResponse(
                1L,
                null,
                null,
                new LocationResponse(1L, 1, 1, 1L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                1f,
                null
        );
        WorkerResponse sut = new WorkerResponse(
                1,
                "Иван Иванов",
                new CoordinatesResponse(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                null,
                person,
                1L
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<WorkerResponse>> violations = factory.getValidator().validate(sut);

            // Assert
            assertThat(violations.size())
                    .as("Fields person.hairColor and person.nationality must be validated with cascading validation.")
                    .isEqualTo(2);
        }
    }

    @Test
    void rejects_creatorId_which_is_null() {
        // Arrange
        WorkerResponse sut = new WorkerResponse(
                1,
                "Иван Иванов",
                new CoordinatesResponse(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                null,
                null,
                null
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<WorkerResponse>> violations = factory.getValidator().validate(sut);
            ConstraintViolation<WorkerResponse> violation = violations.iterator().next();
            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If creatorId is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field creatorId which is null must not be validated.")
                            .isEqualTo("creatorId")
            );
        }
    }

    @Test
    void rejects_creatorId_below_minimum() {
        // Arrange
        WorkerResponse sut = new WorkerResponse(
                1,
                "Иван Иванов",
                new CoordinatesResponse(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                null,
                null,
                0L
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<WorkerResponse>> violations = factory.getValidator().validate(sut);
            ConstraintViolation<WorkerResponse> violation = violations.iterator().next();
            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If creatorId is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field creatorId less than 1 must not be validated.")
                            .isEqualTo("creatorId")
            );
        }
    }

    @Test
    void rejects_creatorId_which_equals_to_long_minimum() {
        // Arrange
        WorkerResponse sut = new WorkerResponse(
                1,
                "Иван Иванов",
                new CoordinatesResponse(1L, 0, 0L),
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                1d,
                1,
                ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                null,
                null,
                null,
                Long.MIN_VALUE
        );

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<WorkerResponse>> violations = factory.getValidator().validate(sut);
            ConstraintViolation<WorkerResponse> violation = violations.iterator().next();
            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If creatorId is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),
                    () -> assertThat(actualPropertyName)
                            .as("Field creatorId equal to Long.MIN_VALUE must not be validated.")
                            .isEqualTo("creatorId")
            );
        }
    }

    @Test
    void serializes_worker_with_expected_property_names() {
        // Arrange
        try {
            WorkerResponse sut = new WorkerResponse(
                    1,
                    "Иван Иванов",
                    new CoordinatesResponse(1L, 0, 0L),
                    ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                    null,
                    1d,
                    1,
                    ZonedDateTime.parse("2026-10-16T00:00:00+03:00"),
                    Position.DIRECTOR,
                    Status.HIRED,
                    null,
                    1L
            );
            JsonbConfig config = new JsonbConfig().withNullValues(true);

            try (Jsonb jsonb = JsonbBuilder.create(config)) {
                // Act
                String serialized = jsonb.toJson(sut);

                try (JsonReader reader = Json.createReader(new StringReader(serialized))) {
                    JsonObject actual = reader.readObject();

                    // Assert
                    assertThat(actual.keySet())
                            .as("The Worker JSON must contain the field names from the API contract.")
                            .containsExactlyInAnyOrder(
                                    "id",
                                    "name",
                                    "coordinates",
                                    "creation_date",
                                    "organization",
                                    "salary",
                                    "rating",
                                    "start_date",
                                    "position",
                                    "status",
                                    "person",
                                    "creator_id"
                            );
                }
            }
        } catch (Exception exception) {
            throw new AssertionError("Worker JSON serialization failed.", exception);
        }
    }
}
