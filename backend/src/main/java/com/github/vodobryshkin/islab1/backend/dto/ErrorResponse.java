package com.github.vodobryshkin.islab1.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO с описанием ошибки для ответов 400, 401, 403, 404 и 409.
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
    private String message;
}
