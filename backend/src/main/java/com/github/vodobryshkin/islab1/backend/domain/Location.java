package com.github.vodobryshkin.islab1.backend.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

/**
 * @author vodobryshkin
 * @since 18.09.2026 14:54
 */
@Embeddable
public class Location {
    @NotNull
    private Integer x; //Поле не может быть null

    @NotNull
    private Integer y; //Поле не может быть null

    @NotNull
    private Long z;    //Поле не может быть null
}
