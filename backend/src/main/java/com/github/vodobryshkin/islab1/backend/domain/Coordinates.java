package com.github.vodobryshkin.islab1.backend.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Класс, который отвечает за представление координат. Встраивается в JPA-сущности.
 *
 * @author vodobryshkin
 * @since 18.09.2026 14:52
 */
@Embeddable
public class Coordinates {
    @Min(value = -953)
    @NotNull
    private Integer x; // Значение поля должно быть больше -954, Поле не может быть null

    @Min(value = -391)
    @NotNull
    private Long y;    // Значение поля должно быть больше -392, Поле не может быть null
}
