package com.github.vodobryshkin.islab1.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.ZonedDateTime;

/**
 * Класс, который отвечает за представление человека. Является JPA-сущностью.
 *
 * @author vodobryshkin
 * @since 18.09.2026 14:55
 */
@Entity
@Table(name = "persons")
public class Person {
    @Id
    @SequenceGenerator(name = "persons_seq", sequenceName = "persons_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "persons_seq")
    @Column(name = "id")
    private Integer id;

    @Column(name = "eye_color")
    @Enumerated(EnumType.STRING)
    private Color eyeColor;                   // Поле может быть null

    @Column(name = "hair_color")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Color hairColor;                  // Поле не может быть null

    @Embedded
    @NotNull
    @Valid
    private Location location;                // Поле не может быть null

    @Column(name = "birthday", columnDefinition = "TIMESTAMPTZ")
    @NotNull
    private ZonedDateTime birthday;           // Поле не может быть null

    @Column(name = "weight")
    @NotNull
    @Positive
    private Float weight;                     // Поле не может быть null,
    // Значение поля должно быть больше 0

    @Column(name = "nationality")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Country nationality;              // Поле не может быть null
}
