package com.github.vodobryshkin.islab1.backend.dto;

import com.github.vodobryshkin.islab1.backend.domain.Location;
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
    @Positive
    private Long id;

    @NotNull
    private Integer x;

    @NotNull
    private Integer y;

    @NotNull
    private Long z;

    /**
     * Создаёт ответ из сохранённой локации.
     *
     * @param location локация
     */
    public LocationResponse(Location location) {
        this(location.getId(), location.getX(), location.getY(), location.getZ());
    }
}
