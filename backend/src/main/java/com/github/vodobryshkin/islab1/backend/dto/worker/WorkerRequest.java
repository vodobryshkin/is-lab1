package com.github.vodobryshkin.islab1.backend.dto.worker;

import com.github.vodobryshkin.islab1.backend.domain.Organization;
import com.github.vodobryshkin.islab1.backend.domain.Position;
import com.github.vodobryshkin.islab1.backend.domain.Status;
import com.github.vodobryshkin.islab1.backend.domain.Worker;
import com.github.vodobryshkin.islab1.backend.dto.corrdinates.CoordinatesRequest;
import com.github.vodobryshkin.islab1.backend.dto.organization.OrganizationRequest;
import com.github.vodobryshkin.islab1.backend.dto.person.PersonRequest;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.ZonedDateTime;
import lombok.Data;

/**
 * DTO с данными работника. Организация задаётся ID существующей или новым объектом.
 */
@Data
public class WorkerRequest {
    @NotBlank
    @Size(max = 255)
    @Pattern(regexp = "^[\\p{L}\\d\\s_.'\"-]{1,255}$")
    private String name;

    @NotNull
    @Valid
    private CoordinatesRequest coordinates;

    @NotNull
    @Positive
    @DecimalMax("1.7976931348623157E308")
    private Double salary;

    @NotNull
    @Positive
    private Integer rating;

    @JsonbProperty("start_date")
    @NotNull
    private ZonedDateTime startDate;

    private Position position;

    private Status status;

    @Valid
    private PersonRequest person;

    @JsonbProperty("organization_id")
    @Positive
    private Long organizationId;

    @Valid
    private OrganizationRequest organization;

    /**
     * Проверяет, что организация не задана одновременно ID и новым объектом.
     *
     * @return true, если задано не больше одного из полей
     */
    @AssertTrue(message = "Use either organization_id or organization.")
    public boolean isOrganizationValid() {
        return organizationId == null || organization == null;
    }

    /**
     * Создаёт работника из запроса без ID, создателя и даты создания.
     *
     * @return работник
     */
    public Worker toEntity() {
        Organization target = organization == null ? null : organization.toEntity();

        if (organizationId != null) {
            target = new Organization(organizationId, null, 0, null, null, null, null, null);
        }

        return new Worker(null, null, name, coordinates.toEntity(), null, target, salary, rating,
                startDate, position, status, person == null ? null : person.toEntity());
    }
}
