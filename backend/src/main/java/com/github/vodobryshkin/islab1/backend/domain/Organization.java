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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Класс, который отвечает за представление организации. Является JPA-сущностью.
 *
 * @author vodobryshkin
 * @since 18.09.2026 14:53
 */
@Entity
@Table(name = "organizations")
@AllArgsConstructor
@NoArgsConstructor
public class Organization {
    @Id
    @SequenceGenerator(
            name = "organizations_seq",
            sequenceName = "organizations_organization_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organizations_seq")
    @Column(name = "organization_id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "official_address_id", nullable = false)
    @NotNull
    private Address officialAddress; // Поле не может быть null

    @Column(name = "annual_turnover", nullable = false)
    @Positive
    private float annualTurnover;    // Значение поля должно быть больше 0

    @Column(name = "employees_count", nullable = false)
    @NotNull
    @Positive
    private Long employeesCount;     // Поле не может быть null, Значение поля должно быть больше 0

    @Column(name = "full_name", unique = true, columnDefinition = "TEXT", nullable = false)
    @NotBlank
    private String fullName;         // Значение этого поля должно быть уникальным,
    // Строка не может быть пустой, Поле не может быть null

    @Column(name = "rating", nullable = false)
    @NotNull
    @Positive
    private Integer rating;          // Поле не может быть null, Значение поля должно быть больше 0

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private OrganizationType type;   // Поле может быть null

    @ManyToOne(optional = false)
    @JoinColumn(name = "postal_address_id", nullable = false)
    @NotNull
    private Address postalAddress;   // Поле не может быть null
}
