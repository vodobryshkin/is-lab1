package com.github.vodobryshkin.islab1.backend.dto;

import com.github.vodobryshkin.islab1.backend.domain.Organization;
import com.github.vodobryshkin.islab1.backend.domain.OrganizationType;
import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO с данными сущности Organization, полученными с сервера.
 *
 * @author vodobryshkin
 * @since 15.10.2026 18:44
 */
@Data
@AllArgsConstructor
public class OrganizationResponse {
    @NotNull
    @Positive
    private Long id;

    @JsonbProperty("official_address")
    @NotNull
    @Valid
    private AddressResponse officialAddress;

    @JsonbProperty("annual_turnover")
    @Positive
    @DecimalMax("3.4028235E38")
    private float annualTurnover;

    @JsonbProperty("employees_count")
    @NotNull
    @Positive
    private Long employeesCount;

    @JsonbProperty("full_name")
    @NotBlank
    private String fullName;

    @NotNull
    @Positive
    private Integer rating;

    @JsonbNillable
    private OrganizationType type;

    @JsonbProperty("postal_address")
    @NotNull
    @Valid
    private AddressResponse postalAddress;

    /**
     * Создаёт ответ из сохранённой организации с адресами и их локациями.
     *
     * @param organization организация
     */
    public OrganizationResponse(Organization organization) {
        this(organization.getId(), new AddressResponse(organization.getOfficialAddress()),
                organization.getAnnualTurnover(), organization.getEmployeesCount(),
                organization.getFullName(), organization.getRating(), organization.getType(),
                new AddressResponse(organization.getPostalAddress()));
    }
}
