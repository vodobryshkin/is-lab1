package com.github.vodobryshkin.islab1.backend.dto;

import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO с ID созданного работника и его организации.
 */
@Data
@AllArgsConstructor
public class CreateWorkerResponse {
    private int id;

    @JsonbNillable
    @JsonbProperty("organization_id")
    private Long organizationId;
}
