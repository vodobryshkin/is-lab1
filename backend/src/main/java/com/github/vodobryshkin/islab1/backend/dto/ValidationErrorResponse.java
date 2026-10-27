package com.github.vodobryshkin.islab1.backend.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO с нарушениями полей тела запроса для ответа 422.
 */
@Data
@AllArgsConstructor
public class ValidationErrorResponse {
    private List<ValidationIssue> errors;
}
