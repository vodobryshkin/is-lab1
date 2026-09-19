package com.github.vodobryshkin.islab1.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * Класс, который отвечает за представление географического адреса. Является JPA-сущностью.
 *
 * @author vodobryshkin
 * @since 18.09.2026 14:53
 */
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @SequenceGenerator(
            name = "addresses_seq",
            sequenceName = "addresses_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "addresses_seq")
    @Column(name = "id")
    private Integer id;

    @Column(name = "street", columnDefinition = "TEXT")
    @NotBlank
    private String street; // Строка не может быть пустой, Поле не может быть null

    @Embedded
    @Valid
    private Location town; // Поле может быть null
}
