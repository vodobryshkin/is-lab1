package com.github.vodobryshkin.islab1.backend.repository;

import com.github.vodobryshkin.islab1.backend.domain.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;

/**
 * Хранилище пользователей.
 */
@ApplicationScoped
public class UserRepository {
    @PersistenceContext(unitName = "is-lab1")
    private EntityManager entityManager;

    /**
     * Ищет пользователя по логину.
     *
     * @param username логин
     * @return пользователь или пустой результат
     */
    public Optional<User> findByUsername(String username) {
        return entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    /**
     * Сохраняет нового пользователя.
     *
     * @param user пользователь
     */
    public void save(User user) {
        entityManager.persist(user);
        entityManager.flush();
    }
}
