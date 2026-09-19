package com.github.vodobryshkin.islab1.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * @author vodobryshkin
 * @since 18.09.2026 14:53
 */
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @SequenceGenerator(name = "addresses_seq", sequenceName = "addresses_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "addresses_seq")
    @Column(name = "id")
    private Integer id;

    @Column(name = "street", columnDefinition = "TEXT")
    @NotBlank
    private String street; //Строка не может быть пустой, Поле не может быть null

    @Embedded
    @Valid
    private Location town; //Поле может быть null
}
