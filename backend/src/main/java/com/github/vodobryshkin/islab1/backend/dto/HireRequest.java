package com.github.vodobryshkin.islab1.backend.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * DTO с ID организации, в которую принимается работник.
 */
@Data
public class HireRequest {
    @JsonbProperty("organization_id")
    @NotNull
    @Positive
    private Long organizationId;
}
