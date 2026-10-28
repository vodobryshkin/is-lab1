package com.github.vodobryshkin.islab1.backend.dto.corrdinates;

import com.github.vodobryshkin.islab1.backend.domain.Coordinates;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO с данными сущности Coordinates, полученными с сервера.
 *
 * @author vodobryshkin
 * @since 15.10.2026 18:16
 */
@Data
@AllArgsConstructor
public class CoordinatesResponse {
    @NotNull
    @Positive
    private Long id;

    @NotNull
    @Min(value = -953)
    private Integer x;

    @NotNull
    @Min(value = -391)
    private Long y;

    /**
     * Создаёт ответ из сохранённых координат.
     *
     * @param coordinates координаты
     */
    public CoordinatesResponse(Coordinates coordinates) {
        this(coordinates.getId(), coordinates.getX(), coordinates.getY());
    }
}
