package com.github.vodobryshkin.islab1.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Класс, который отвечает за представление человека. Является JPA-сущностью.
 *
 * @author vodobryshkin
 * @since 18.09.2026 14:55
 */
@Entity
@Table(name = "persons")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @Id
    @SequenceGenerator(
            name = "persons_seq",
            sequenceName = "persons_person_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "persons_seq")
    @Column(name = "person_id")
    @Positive
    private Long id;

    @Column(name = "eye_color")
    @Enumerated(EnumType.STRING)
    private Color eyeColor;                   // Поле может быть null

    @Column(name = "hair_color", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private Color hairColor;                  // Поле не может быть null

    @ManyToOne(optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    @NotNull
    @Valid
    private Location location;                // Поле не может быть null

    @Column(name = "birthday", nullable = false, columnDefinition = "TIMESTAMPTZ")
    @NotNull
    private ZonedDateTime birthday;           // Поле не может быть null

    @Column(name = "weight", nullable = false)
    @NotNull
    @Positive
    // +∞ недопустима; NaN, 0 и отрицательные значения отклоняет @Positive.
    @DecimalMax("3.4028235E38")
    private Float weight;                     // Поле не может быть null,
    // Значение поля должно быть больше 0

    @Column(name = "nationality", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private Country nationality;              // Поле не может быть null

    @OneToOne
    @JoinColumn(name = "worker_id", unique = true)
    @Valid
    private Worker worker;
}
