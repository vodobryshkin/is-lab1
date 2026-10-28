package com.github.vodobryshkin.islab1.backend.dto.organization;

import com.github.vodobryshkin.islab1.backend.domain.Address;
import com.github.vodobryshkin.islab1.backend.domain.Organization;
import com.github.vodobryshkin.islab1.backend.domain.OrganizationType;
import com.github.vodobryshkin.islab1.backend.dto.address.AddressRequest;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * DTO с данными организации. Каждый адрес задаётся новым объектом или ID существующего.
 */
@Data
public class OrganizationRequest {
    @JsonbProperty("official_address")
    @Valid
    private AddressRequest officialAddress;

    @JsonbProperty("official_address_id")
    @Positive
    private Long officialAddressId;

    @JsonbProperty("annual_turnover")
    @NotNull
    @Positive
    @DecimalMax("3.4028235E38")
    private Float annualTurnover;

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

    private OrganizationType type;

    @JsonbProperty("postal_address")
    @Valid
    private AddressRequest postalAddress;

    @JsonbProperty("postal_address_id")
    @Positive
    private Long postalAddressId;

    /**
     * Проверяет, что официальный адрес задан ровно одним способом.
     *
     * @return true, если задан либо official_address, либо official_address_id
     */
    @AssertTrue(message = "Use either official_address or official_address_id.")
    public boolean isOfficialAddressValid() {
        return officialAddress == null ^ officialAddressId == null;
    }

    /**
     * Проверяет, что почтовый адрес задан ровно одним способом.
     *
     * @return true, если задан либо postal_address, либо postal_address_id
     */
    @AssertTrue(message = "Use either postal_address or postal_address_id.")
    public boolean isPostalAddressValid() {
        return postalAddress == null ^ postalAddressId == null;
    }

    /**
     * Создаёт организацию из запроса.
     *
     * @return организация
     */
    public Organization toEntity() {
        Address official = officialAddress == null
                ? new Address(officialAddressId, null, null)
                : officialAddress.toEntity();
        Address postal = postalAddress == null
                ? new Address(postalAddressId, null, null)
                : postalAddress.toEntity();

        return new Organization(null, official, annualTurnover, employeesCount, fullName, rating,
                type, postal);
    }
}
