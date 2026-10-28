package com.github.vodobryshkin.islab1.backend.dto.person;

import com.github.vodobryshkin.islab1.backend.domain.Color;
import com.github.vodobryshkin.islab1.backend.domain.Country;
import com.github.vodobryshkin.islab1.backend.domain.Person;
import com.github.vodobryshkin.islab1.backend.dto.location.LocationResponse;
import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.ZoneOffset;
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

    @JsonbNillable
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
    @DecimalMax("3.4028235E38")
    private Float weight;

    @NotNull
    private Country nationality;

    /**
     * Создаёт ответ из сохранённой персоны с её локацией, день рождения -- в UTC.
     *
     * @param person персона
     */
    public PersonResponse(Person person) {
        this(person.getId(), person.getEyeColor(), person.getHairColor(),
                new LocationResponse(person.getLocation()),
                person.getBirthday().withZoneSameInstant(ZoneOffset.UTC), person.getWeight(),
                person.getNationality());
    }
}
