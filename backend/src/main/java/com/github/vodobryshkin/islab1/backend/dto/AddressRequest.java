package com.github.vodobryshkin.islab1.backend.dto;

import com.github.vodobryshkin.islab1.backend.domain.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO с данными адреса.
 */
@Data
public class AddressRequest {
    @NotBlank
    private String street;

    @Valid
    private LocationRequest town;

    /**
     * Создаёт адрес из запроса.
     *
     * @return адрес
     */
    public Address toEntity() {
        return new Address(null, street, town == null ? null : town.toEntity());
    }
}
