package com.github.vodobryshkin.islab1.backend.dto;

import com.github.vodobryshkin.islab1.backend.domain.Position;
import com.github.vodobryshkin.islab1.backend.domain.Status;
import com.github.vodobryshkin.islab1.backend.domain.Worker;
import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.ZoneOffset;
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

    @NotBlank
    private String name;

    @NotNull
    @Valid
    private CoordinatesResponse coordinates;

    @JsonbProperty("creation_date")
    @NotNull
    private ZonedDateTime creationDate;

    @JsonbNillable
    @Valid
    private OrganizationResponse organization;

    @Positive
    @DecimalMax("1.7976931348623157E308")
    private double salary;

    @Positive
    private int rating;

    @JsonbProperty("start_date")
    @NotNull
    private ZonedDateTime startDate;

    @JsonbNillable
    private Position position;
    @JsonbNillable
    private Status status;

    @JsonbNillable
    @Valid
    private PersonResponse person;

    @JsonbProperty("creator_id")
    @NotNull
    @Positive
    private Long creatorId;

    /**
     * Создаёт ответ из сохранённого работника с вложенными объектами; даты — в UTC.
     *
     * @param worker работник
     */
    public WorkerResponse(Worker worker) {
        this(worker.getId(), worker.getName(), new CoordinatesResponse(worker.getCoordinates()),
                worker.getCreationDate().withZoneSameInstant(ZoneOffset.UTC),
                worker.getOrganization() == null
                        ? null
                        : new OrganizationResponse(worker.getOrganization()),
                worker.getSalary(), worker.getRating(),
                worker.getStartDate().withZoneSameInstant(ZoneOffset.UTC), worker.getPosition(),
                worker.getStatus(),
                worker.getPerson() == null ? null : new PersonResponse(worker.getPerson()),
                worker.getUser().getId());
    }
}
