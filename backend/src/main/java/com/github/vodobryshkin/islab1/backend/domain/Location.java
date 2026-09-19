package com.github.vodobryshkin.islab1.backend.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

/**
 * @author vodobryshkin
 * @since 18.09.2026 14:54
 */
@Embeddable
public class Location {
    private Integer x;
    private Integer y;
    private Long z;

    @AssertTrue
    public boolean isComplete() {
        return x != null && y != null && z != null;
    }
}
