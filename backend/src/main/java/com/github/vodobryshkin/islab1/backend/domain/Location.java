package com.github.vodobryshkin.islab1.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Класс, который отвечает за представление локации. Встраивается в JPA-сущности.
 *
 * @author vodobryshkin
 * @since 18.09.2026 14:54
 */
@Entity
@Table(name = "locations")
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    @Id
    @SequenceGenerator(
            name = "locations_seq",
            sequenceName = "locations_location_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "locations_seq")
    @Column(name = "location_id")
    private Long id;

    @Column(name = "x", nullable = false)
    @NotNull
    private Integer x; // Поле не может быть null

    @Column(name = "y", nullable = false)
    @NotNull
    private Integer y; // Поле не может быть null

    @Column(name = "z", nullable = false)
    @NotNull
    private Long z;    // Поле не может быть null
}
