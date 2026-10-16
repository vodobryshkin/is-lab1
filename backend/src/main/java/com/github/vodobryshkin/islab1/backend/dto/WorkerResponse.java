package com.github.vodobryshkin.islab1.backend.dto;

import com.github.vodobryshkin.islab1.backend.domain.Position;
import com.github.vodobryshkin.islab1.backend.domain.Status;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO с данными сущности Worker, полученными с сервера.
 *
 * @author vodobryshkin
 * @since 15.10.2026 17:41
 */
@Data
@AllArgsConstructor
public class WorkerResponse {
    @NotNull
    @Positive
    private Integer id;

    @Size(min = 1)
    @NotBlank
    private String name;

    @NotNull
    @Valid
    private CoordinatesResponse coordinates;

    @JsonbProperty("creation_date")
    @NotNull
    private ZonedDateTime creationDate;

    @Valid
    private OrganizationResponse organization;

    @Positive
    private double salary;

    @Positive
    private int rating;

    @JsonbProperty("start_date")
    @NotNull
    private ZonedDateTime startDate;

    private Position position;
    private Status status;

    @Valid
    private PersonResponse person;

    @JsonbProperty("creator_id")
    @NotNull
    @Positive
    private Long creatorId;

    /**
     * Проверяет, что salary не является положительной бесконечностью.
     *
     * @return true, если salary является бесконечностью, иначе false
     */
    @AssertTrue
    public boolean isSalary() {
        return salary != Float.POSITIVE_INFINITY;
    }
}
