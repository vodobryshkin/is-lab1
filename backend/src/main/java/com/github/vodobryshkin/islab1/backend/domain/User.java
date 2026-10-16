package com.github.vodobryshkin.islab1.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Класс, который отвечает за представление пользователя системы. Является JPA-сущностью.
 *
 * @author vodobryshkin
 * @since 22.09.2026 11:49
 */
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @SequenceGenerator(name = "users_seq", sequenceName = "users_user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @Column(name = "user_id")
    @Positive
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 64)
    @NotBlank
    @Size(max = 64)
    private String username;

    @OneToMany(mappedBy = "user")
    private List<Worker> workers;
}
