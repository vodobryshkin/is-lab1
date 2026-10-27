package com.github.vodobryshkin.islab1.backend.dto;

import com.github.vodobryshkin.islab1.backend.domain.Location;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * DTO с новой локацией или ссылкой на существующую по ID.
 */
@Data
public class LocationRequest {
    @Positive
    private Long id;

    @NotNull
    private Integer x;

    @NotNull
    private Integer y;

    @NotNull
    private Long z;

    /**
     * Создаёт локацию из запроса.
     *
     * @return локация
     */
    public Location toEntity() {
        return new Location(id, x, y, z);
    }
}
