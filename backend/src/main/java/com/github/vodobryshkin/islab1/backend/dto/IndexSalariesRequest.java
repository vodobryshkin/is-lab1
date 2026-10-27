package com.github.vodobryshkin.islab1.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * DTO с коэффициентом индексации зарплат.
 */
@Data
public class IndexSalariesRequest {
    @NotNull
    @Positive
    @DecimalMax("1.7976931348623157E308")
    private Double coefficient;
}
