package com.github.vodobryshkin.islab1.backend.dto;

import com.github.vodobryshkin.islab1.backend.domain.Color;
import com.github.vodobryshkin.islab1.backend.domain.Country;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO с данными сущности Person, полученными с сервера.
 *
 * @author vodobryshkin
 * @since 15.10.2026 18:23
 */
@Data
@AllArgsConstructor
public class PersonResponse {
    @NotNull
    @Positive
    private Long id;

    @JsonbProperty("eye_color")
    private Color eyeColor;

    @JsonbProperty("hair_color")
    @NotNull
    private Color hairColor;

    @NotNull
    @Valid
    private LocationResponse location;

    @NotNull
    private ZonedDateTime birthday;

    @Positive
    private float weight;

    @NotNull
    private Country nationality;
}
