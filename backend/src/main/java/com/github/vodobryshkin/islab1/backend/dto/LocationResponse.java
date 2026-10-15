package com.github.vodobryshkin.islab1.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO с данными сущности Location, полученными с сервера.
 *
 * @author vodobryshkin
 * @since 15.10.2026 18:30
 */
@Data
@AllArgsConstructor
public class LocationResponse {
    @NotNull
    private Integer x;

    @NotNull
    private Integer y;

    @NotNull
    private Long z;

    @NotNull
    @Positive
    private Long id;
}
