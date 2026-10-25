package com.github.vodobryshkin.islab1.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Логин и пароль из запросов регистрации и входа.
 *
 * <p>Ограничения повторяют {@code RegisterRequest} контракта. Класс намеренно не имеет
 * {@code toString}, {@code equals} и {@code hashCode}, чтобы пароль не попал в журналы.
 */
@Getter
@AllArgsConstructor
public class CredentialsRequest {
    @NotNull(message = "Username must not be null.")
    @NotEmpty(message = "Username must not be empty.")
    @Size(min = 3, max = 64, message = "Username must contain between 3 and 64 characters.")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,64}$",
            message = "Username may contain only Latin letters, digits and underscores.")
    private final String username;

    @NotNull(message = "Password must not be null.")
    @NotEmpty(message = "Password must not be empty.")
    @Size(min = 6, max = 32, message = "Password must contain between 6 and 32 characters.")
    @Pattern(regexp = "^[a-zA-Z0-9_]{6,32}$",
            message = "Password may contain only Latin letters, digits and underscores.")
    private final String password;
}
