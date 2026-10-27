package com.github.vodobryshkin.islab1.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO с публичными данными вошедшего пользователя.
 */
@Data
@AllArgsConstructor
public class CurrentUserResponse {
    private long id;

    private String username;
}
