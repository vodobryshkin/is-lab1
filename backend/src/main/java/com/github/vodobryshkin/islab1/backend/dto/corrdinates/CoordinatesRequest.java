package com.github.vodobryshkin.islab1.backend.dto.corrdinates;

import com.github.vodobryshkin.islab1.backend.domain.Coordinates;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * DTO с новыми координатами или ссылкой на существующие по ID.
 */
@Data
public class CoordinatesRequest {
    @Positive
    private Long id;

    @NotNull
    @Min(-953)
    private Integer x;

    @NotNull
    @Min(-391)
    private Long y;

    /**
     * Создаёт координаты из запроса.
     *
     * @return координаты
     */
    public Coordinates toEntity() {
        return new Coordinates(id, x, y);
    }
}
