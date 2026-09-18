package com.github.vodobryshkin.islab1.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.ZonedDateTime;

/**
 * @author vodobryshkin
 * @since 18.09.2026 14:55
 */
@Entity
@Table(name = "persons")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Integer id;

    @Column(name = "eye_color")
    @Enumerated(EnumType.STRING)
    private Color eyeColor;                   //Поле может быть null

    @Column(name = "hair_color")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Color hairColor;                  //Поле не может быть null

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "location_x")),
            @AttributeOverride(name = "y", column = @Column(name = "location_y")),
            @AttributeOverride(name = "z", column = @Column(name = "location_z"))
    })
    private Location location;                //Поле не может быть null

    @Column(name = "birthday", columnDefinition = "TIMESTAMPTZ")
    @NotNull
    private ZonedDateTime birthday;           //Поле не может быть null


    @Column(name = "weight")
    @NotNull
    @Positive
    private Float weight;                     //Поле не может быть null, Значение поля должно быть больше 0

    @Column(name = "nationality")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Country nationality;              //Поле не может быть null
}
