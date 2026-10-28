package com.github.vodobryshkin.islab1.backend.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO с нарушением правила в одном поле тела запроса.
 */
@Data
@AllArgsConstructor
public class ValidationIssue {
    private String field;

    private String code;

    private String message;
}
