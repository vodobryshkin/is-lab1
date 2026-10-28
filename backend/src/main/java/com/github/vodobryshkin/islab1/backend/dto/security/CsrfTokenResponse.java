package com.github.vodobryshkin.islab1.backend.dto.security;

import jakarta.json.bind.annotation.JsonbProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO с CSRF-токеном текущей сессии.
 */
@Data
@AllArgsConstructor
public class CsrfTokenResponse {
    @JsonbProperty("csrf_token")
    private String csrfToken;
}
