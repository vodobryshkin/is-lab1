package com.github.vodobryshkin.islab1.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Класс, который отвечает за представление географического адреса. Является JPA-сущностью.
 *
 * @author vodobryshkin
 * @since 18.09.2026 14:53
 */
@Entity
@Table(name = "addresses")
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @Id
    @SequenceGenerator(
            name = "addresses_seq",
            sequenceName = "addresses_address_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "addresses_seq")
    @Column(name = "address_id")
    @Positive
    private Long id;

    @Column(name = "street", nullable = false, columnDefinition = "TEXT")
    @NotBlank
    private String street; // Строка не может быть пустой, Поле не может быть null

    @ManyToOne
    @JoinColumn(name = "town_id")
    @Valid
    private Location town; // Поле может быть null
}
