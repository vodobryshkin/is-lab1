package com.github.vodobryshkin.islab1.backend.dto;

import com.github.vodobryshkin.islab1.backend.domain.Color;
import com.github.vodobryshkin.islab1.backend.domain.Country;
import com.github.vodobryshkin.islab1.backend.domain.Person;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.ZonedDateTime;
import lombok.Data;

/**
 * DTO с новой персоной или ссылкой на существующую по ID.
 */
@Data
public class PersonRequest {
    @Positive
    private Long id;

    @JsonbProperty("eye_color")
    private Color eyeColor;

    @JsonbProperty("hair_color")
    @NotNull
    private Color hairColor;

    @NotNull
    @Valid
    private LocationRequest location;

    @NotNull
    private ZonedDateTime birthday;

    @NotNull
    @Positive
    @DecimalMax("3.4028235E38")
    private Float weight;

    @NotNull
    private Country nationality;

    /**
     * Создаёт персону из запроса.
     *
     * @return персона
     */
    public Person toEntity() {
        return new Person(id, eyeColor, hairColor, location == null ? null : location.toEntity(),
                birthday, weight, nationality, null);
    }
}
