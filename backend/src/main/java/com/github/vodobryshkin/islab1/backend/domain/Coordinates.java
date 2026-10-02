package com.github.vodobryshkin.islab1.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Класс, который отвечает за представление координат. Встраивается в JPA-сущности.
 *
 * @author vodobryshkin
 * @since 18.09.2026 14:52
 */
@Entity
@Table(name = "coordinates")
public class Coordinates {
    @Id
    @SequenceGenerator(
            name = "coordinates_seq",
            sequenceName = "coordinates_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "coordinates_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "x")
    @Min(value = -953)
    @NotNull
    private Integer x; // Значение поля должно быть больше -954, Поле не может быть null

    @Column(name = "y")
    @Min(value = -391)
    @NotNull
    private Long y;    // Значение поля должно быть больше -392, Поле не может быть null
}
