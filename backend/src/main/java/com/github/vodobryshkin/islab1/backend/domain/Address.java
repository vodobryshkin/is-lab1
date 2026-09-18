package com.github.vodobryshkin.islab1.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * @author vodobryshkin
 * @since 18.09.2026 14:53
 */
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Integer id;

    @Column(name = "street")
    @NotBlank
    private String street; //Строка не может быть пустой, Поле не может быть null

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "town_x")),
            @AttributeOverride(name = "y", column = @Column(name = "town_y")),
            @AttributeOverride(name = "z", column = @Column(name = "town_z"))
    })
    private Location town; //Поле может быть null
}
