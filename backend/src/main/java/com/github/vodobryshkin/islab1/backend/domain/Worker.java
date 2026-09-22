package com.github.vodobryshkin.islab1.backend.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.ZonedDateTime;

/**
 * Класс, который отвечает за представление работника. Является JPA-сущностью.
 *
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
    private Integer id; // Поле не может быть null, Значение поля должно быть больше 0,
    // Значение этого поля должно быть уникальным,
    // Значение этого поля должно генерироваться автоматически

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "name")
    @NotBlank
    private String name; // Поле не может быть null, Строка не может быть пустой

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "coordinate_x")),
            @AttributeOverride(name = "y", column = @Column(name = "coordinate_y"))
    })
    @NotNull
    @Valid
    private Coordinates coordinates; // Поле не может быть null

    @Column(name = "creation_date")
    @NotNull
    private ZonedDateTime creationDate; // Поле не может быть null,
    // Значение этого поля должно генерироваться автоматически

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization; // Поле может быть null

    @Column(name = "salary")
    @Positive
    private double salary; // Значение поля должно быть больше 0

    @Column(name = "rating")
    @NotNull
    @Positive
    private Integer rating; // Поле не может быть null, Значение поля должно быть больше 0

    @Column(name = "start_date")
    @NotNull
    private ZonedDateTime startDate; // Поле не может быть null

    @Column(name = "position")
    @Enumerated(EnumType.STRING)
    private Position position; // Поле может быть null

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status; // Поле может быть null

    @OneToOne(mappedBy = "worker")
    private Person person; // Поле может быть null

    /**
     * Метод для автоматической генерации времени создания работника.
     */
    @PrePersist
    protected void onCreate() {
        if (creationDate == null) {
            creationDate = ZonedDateTime.now();
        }
    }
}
