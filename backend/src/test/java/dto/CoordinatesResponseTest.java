package dto;

import com.github.vodobryshkin.islab1.backend.dto.CoordinatesResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * Тестовый класс для тестирования класса CoordinatesResponse.
 *
 * @author vodobryshkin
 * @since 15.10.2026 19:18
 */
public class CoordinatesResponseTest {
    @Test
    void passes_correct_DTO() {
        CoordinatesResponse sut = new CoordinatesResponse(1L, 1, 0L);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            int actualSize  = factory.getValidator().validate(sut).size();

            assertAll(
                    () -> assertThat(actualSize)
                            .as("If there are no errors in the DTO, then violations should not be thrown out.")
                            .isEqualTo(0)
            );
        }
    }

    @Test
    void rejects_id_which_is_null() {
        // Arrange
        CoordinatesResponse sut = new CoordinatesResponse(null, 1, 0L);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<CoordinatesResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<CoordinatesResponse> violation = violations.iterator().next();

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
        CoordinatesResponse sut = new CoordinatesResponse(forbiddenId, 1, 0L);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<CoordinatesResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<CoordinatesResponse> violation = violations.iterator().next();

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
        Long forbiddenId = Long.MIN_VALUE;
        CoordinatesResponse sut = new CoordinatesResponse(forbiddenId, 1, 0L);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<CoordinatesResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<CoordinatesResponse> violation = violations.iterator().next();

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
        CoordinatesResponse sut = new CoordinatesResponse(1L, null, 0L);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<CoordinatesResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<CoordinatesResponse> violation = violations.iterator().next();

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
    void rejects_x_below_minimum() {
        // Arrange
        Integer forbiddenX = -954;
        CoordinatesResponse sut = new CoordinatesResponse(1L, forbiddenX, 0L);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<CoordinatesResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<CoordinatesResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If x is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Field x less than -953 must not be validated.")
                            .isEqualTo("x")
            );
        }
    }

    @Test
    void rejects_x_which_equals_to_integer_minimum() {
        // Arrange
        Integer forbiddenX = Integer.MIN_VALUE;
        CoordinatesResponse sut = new CoordinatesResponse(1L, forbiddenX, 0L);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<CoordinatesResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<CoordinatesResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If x is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Field x less than -953 must not be validated.")
                            .isEqualTo("x")
            );
        }
    }

    @Test
    void rejects_y_which_is_null() {
        // Arrange
        CoordinatesResponse sut = new CoordinatesResponse(1L, 1, null);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<CoordinatesResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<CoordinatesResponse> violation = violations.iterator().next();

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
    void rejects_y_below_minimum() {
        // Arrange
        Long forbiddenY = -392L;
        CoordinatesResponse sut = new CoordinatesResponse(1L, 1, forbiddenY);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<CoordinatesResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<CoordinatesResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If y is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Field y less than -391 must not be validated.")
                            .isEqualTo("y")
            );
        }
    }

    @Test
    void rejects_y_which_equals_to_integer_minimum() {
        // Arrange
        Long forbiddenY = Long.MIN_VALUE;
        CoordinatesResponse sut = new CoordinatesResponse(1L, 1, forbiddenY);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            // Act
            Set<ConstraintViolation<CoordinatesResponse>> violations =
                    factory.getValidator().validate(sut);

            ConstraintViolation<CoordinatesResponse> violation = violations.iterator().next();

            int actualSize = violations.size();
            String actualPropertyName = violation.getPropertyPath().toString();

            // Assert
            assertAll(
                    () -> assertThat(actualSize)
                            .as("If y is invalid and all other fields are valid, one violation is expected.")
                            .isEqualTo(1),

                    () -> assertThat(actualPropertyName)
                            .as("Field y less than -391 must not be validated.")
                            .isEqualTo("y")
            );
        }
    }
}
