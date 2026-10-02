package com.github.vodobryshkin.islab1.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Класс, который отвечает за представление локации. Встраивается в JPA-сущности.
 *
 * @author vodobryshkin
 * @since 18.09.2026 14:54
 */
@Entity
@Table(name = "locations")
public class Location {
    @Id
    @SequenceGenerator(
            name = "locations_seq",
            sequenceName = "locations_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "locations_seq")
    @Column(name = "id")
    private Integer id;

    @NotNull
    private Integer x; // Поле не может быть null

    @NotNull
    private Integer y; // Поле не может быть null

    @NotNull
    private Long z;    // Поле не может быть null
}
