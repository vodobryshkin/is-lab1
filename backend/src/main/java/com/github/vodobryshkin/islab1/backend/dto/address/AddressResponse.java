package com.github.vodobryshkin.islab1.backend.dto.address;

import com.github.vodobryshkin.islab1.backend.domain.Address;
import com.github.vodobryshkin.islab1.backend.dto.location.LocationResponse;
import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO с данными сущности Address, полученными с сервера.
 *
 * @author vodobryshkin
 * @since 15.10.2026 18:56
 */
@Data
@AllArgsConstructor
public class AddressResponse {
    @NotNull
    @Positive
    private Long id;

    @NotBlank
    private String street;

    @JsonbNillable
    @Valid
    private LocationResponse town;

    /**
     * Создаёт ответ из сохранённого адреса вместе с его локацией.
     *
     * @param address адрес
     */
    public AddressResponse(Address address) {
        this(address.getId(), address.getStreet(),
                address.getTown() == null ? null : new LocationResponse(address.getTown()));
    }
}
