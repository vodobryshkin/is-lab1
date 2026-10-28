package com.github.vodobryshkin.islab1.backend.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.github.vodobryshkin.islab1.backend.dto.common.ErrorResponse;
import com.github.vodobryshkin.islab1.backend.dto.common.ValidationErrorResponse;
import com.github.vodobryshkin.islab1.backend.dto.common.ValidationIssue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Проверяет JSON ошибок: сообщение для 400–409 и нарушения полей для 422.
 */
class ErrorResponseTest {
    private final JsonOf json = new JsonOf();

    @AfterEach
    void close() {
        json.close();
    }

    @Test
    void writes_only_message() {
        assertThat(json.serialize(new ErrorResponse("Coordinates with id=5 do not exist.")))
                .isEqualTo(json.parse("{\"message\":\"Coordinates with id=5 do not exist.\"}"));
    }

    @Test
    void writes_field_code_and_message_of_each_issue() {
        ValidationErrorResponse sut = new ValidationErrorResponse(List.of(
                new ValidationIssue("salary", "MUST_BE_POSITIVE", "salary must be greater than zero."),
                new ValidationIssue("person.location.x", "REQUIRED", "person.location.x is required.")));

        assertThat(json.serialize(sut)).isEqualTo(json.parse(
                """
                {"errors":[
                  {"field":"salary","code":"MUST_BE_POSITIVE","message":"salary must be greater than zero."},
                  {"field":"person.location.x","code":"REQUIRED","message":"person.location.x is required."}
                ]}
                """));
    }
}
