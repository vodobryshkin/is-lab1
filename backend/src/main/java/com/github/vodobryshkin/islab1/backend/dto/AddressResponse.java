package com.github.vodobryshkin.islab1.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO с данными сущности Address, полученными с сервера.
 *
 * @author vodobryshkin
 * @since 15.10.2026 18:56
 */
@Data
@AllArgsConstructor
public class AddressResponse {
    @NotBlank
    private String street;

    @NotNull
    @Positive
    private Long id;

    @Valid
    private LocationResponse town;
}
