package com.github.vodobryshkin.islab1.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * @author vodobryshkin
 * @since 18.09.2026 14:53
 */
@Entity
@Table(name = "organizations")
public class Organization {
    @Id
    @SequenceGenerator(name = "organizations_seq", sequenceName = "organizations_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organizations_seq")
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "official_address_id", nullable = false)
    @NotNull
    private Address officialAddress; //Поле не может быть null

    @Column(name = "annual_turnover")
    @Positive
    private float annualTurnover;    //Значение поля должно быть больше 0

    @Column(name = "employees_count")
    @NotNull
    @Positive
    private Long employeesCount;     //Поле не может быть null, Значение поля должно быть больше 0

    @Column(name = "full_name", unique = true, columnDefinition = "TEXT")
    @NotBlank
    private String fullName;         //Значение этого поля должно быть уникальным, Строка не может быть пустой, Поле не может быть null

    @Column(name = "rating")
    @NotNull
    @Positive
    private Integer rating;          //Поле не может быть null, Значение поля должно быть больше 0

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private OrganizationType type;   //Поле может быть null

    @ManyToOne
    @JoinColumn(name = "postal_address_id", nullable = false)
    @NotNull
    private Address postalAddress;   //Поле не может быть null
}
