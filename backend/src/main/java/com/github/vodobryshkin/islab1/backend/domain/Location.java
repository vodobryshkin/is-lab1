package com.github.vodobryshkin.islab1.backend.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.AssertTrue;

/**
 * Класс, который отвечает за представление локации. Встраивается в JPA-сущности.
 *
 * @author vodobryshkin
 * @since 18.09.2026 14:54
 */
@Embeddable
public class Location {
    private Integer x; // Поле не может быть null
    private Integer y; // Поле не может быть null
    private Long z;    // Поле не может быть null

    @AssertTrue
    public boolean isComplete() {
        return x != null && y != null && z != null;
    }
}
