package com.github.vodobryshkin.islab1.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.ZonedDateTime;

/**
 * @author vodobryshkin
 * @since 18.09.2026 14:52
 */
@Entity
@Table(name = "workers")
public class Worker {
    @Id
    @SequenceGenerator(name = "workers_seq", sequenceName = "workers_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workers_seq")
    @Column(name = "id")
    @Positive
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @Column(name = "name")
    @NotBlank
    private String name; //Поле не может быть null, Строка не может быть пустой

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "coordinate_x")),
            @AttributeOverride(name = "y", column = @Column(name = "coordinate_y"))
    })
    @NotNull
    @Valid
    private Coordinates coordinates; //Поле не может быть null

    @Column(name = "creation_date")
    @NotNull
    private java.time.ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization; //Поле может быть null

    @Column(name = "salary")
    @Positive
    private double salary; //Значение поля должно быть больше 0

    @Column(name = "rating")
    @NotNull
    @Positive
    private Integer rating; //Поле не может быть null, Значение поля должно быть больше 0

    @Column(name = "start_date")
    @NotNull
    private java.time.ZonedDateTime startDate; //Поле не может быть null

    @Column(name = "position")
    @Enumerated(EnumType.STRING)
    private Position position; //Поле может быть null

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status; //Поле может быть null

    @OneToOne
    private Person person; //Поле может быть null

    @PrePersist
    protected void onCreate() {
        if (creationDate == null) {
            creationDate = ZonedDateTime.now();
        }
    }
}
