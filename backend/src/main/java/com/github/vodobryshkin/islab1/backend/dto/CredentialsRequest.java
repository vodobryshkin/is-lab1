package com.github.vodobryshkin.islab1.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO с логином и паролем из запросов регистрации и входа.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CredentialsRequest {
    @NotNull(message = "Username must not be null.")
    @NotEmpty(message = "Username must not be empty.")
    @Size(min = 3, max = 64, message = "Username must contain between 3 and 64 characters.")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,64}$",
            message = "Username may contain only Latin letters, digits and underscores.")
    private String username;

    @NotNull(message = "Password must not be null.")
    @NotEmpty(message = "Password must not be empty.")
    @Size(min = 6, max = 32, message = "Password must contain between 6 and 32 characters.")
    @Pattern(regexp = "^[a-zA-Z0-9_]{6,32}$",
            message = "Password may contain only Latin letters, digits and underscores.")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String password;
}
