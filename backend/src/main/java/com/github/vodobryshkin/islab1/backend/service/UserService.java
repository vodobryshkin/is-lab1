package com.github.vodobryshkin.islab1.backend.service;

import com.github.vodobryshkin.islab1.backend.domain.User;
import com.github.vodobryshkin.islab1.backend.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.Optional;

/**
 * Регистрация пользователей и проверка паролей.
 * Параметры PBKDF2 заданы явно: значения по умолчанию слабее рекомендаций OWASP.
 */
@ApplicationScoped
public class UserService {
    private static final Map<String, String> HASH_PARAMETERS = Map.of(
            "Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA512",
            "Pbkdf2PasswordHash.Iterations", "210000",
            "Pbkdf2PasswordHash.SaltSizeBytes", "32",
            "Pbkdf2PasswordHash.KeySizeBytes", "64"
    );

    private final UserRepository userRepository;
    private final Pbkdf2PasswordHash passwordHash;

    /** Хеш для проверки пароля неизвестного логина за то же время, что и известного. */
    private final String unusedHash;

    /**
     * Создаёт сервис.
     *
     * @param userRepository хранилище пользователей
     * @param passwordHash реализация PBKDF2 из Jakarta Security
     */
    @Inject
    public UserService(UserRepository userRepository, Pbkdf2PasswordHash passwordHash) {
        this.userRepository = userRepository;
        this.passwordHash = passwordHash;
        passwordHash.initialize(HASH_PARAMETERS);
        this.unusedHash = passwordHash.generate("unused-password-hash".toCharArray());
    }

    /** Нужен CDI для создания клиентского прокси; прикладной код его не вызывает. */
    protected UserService() {
        this.userRepository = null;
        this.passwordHash = null;
        this.unusedHash = null;
    }

    /**
     * Создаёт пользователя с хешем пароля без входа.
     * Занятый логин отклоняет уникальный индекс БД, и транзакция откатывается.
     *
     * @param username логин
     * @param password пароль
     * @return ID нового пользователя
     */
    @Transactional
    public Long register(String username, String password) {
        User user = new User(null, username, passwordHash.generate(password.toCharArray()), null);
        userRepository.save(user);
        return user.getId();
    }

    /**
     * Проверяет логин и пароль.
     *
     * @param username логин
     * @param password пароль
     * @return пользователь, если пароль верный
     */
    public Optional<User> checkCredentials(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        String hash = user.map(User::getPasswordHash).orElse(unusedHash);
        boolean matches = passwordHash.verify(password.toCharArray(), hash);
        return user.filter(found -> matches);
    }

    /**
     * Ищет пользователя по логину.
     *
     * @param username логин
     * @return пользователь или пустой результат
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
