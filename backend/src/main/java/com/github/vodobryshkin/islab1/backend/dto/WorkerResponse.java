package com.github.vodobryshkin.islab1.backend.dto;

import com.github.vodobryshkin.islab1.backend.domain.Position;
import com.github.vodobryshkin.islab1.backend.domain.Status;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.ZonedDateTime;

/**
 * DTO с данными сущности Worker, полученными с сервера.
 *
 * @author vodobryshkin
 * @since 15.10.2026 17:41
 */
@Data
@AllArgsConstructor
public class WorkerResponse {
    @Size(min = 1)
    @Pattern(regexp = ".*\\S.*")
    @NotNull
    private String name;

    @Positive
    private double salary;

    @Positive
    private int rating;

    @JsonbProperty("start_date")
    @NotNull
    private ZonedDateTime startDate;

    private Position position;
    private Status status;

    @NotNull
    @Positive
    private Integer id;

    @JsonbProperty("creator_id")
    @NotNull
    @Positive
    private Long creatorId;

    @JsonbProperty("creation_date")
    private ZonedDateTime creationDate;

    @Valid
    private OrganizationResponse organization;

    @NotNull
    @Valid
    private CoordinatesResponse coordinates;

    @Valid
    private PersonResponse person;
}
