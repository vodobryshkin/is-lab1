package com.github.vodobryshkin.islab1.backend.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.vodobryshkin.islab1.backend.ConstraintViolations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Проверяет логин по правилам контракта и БД и обязательность хеша пароля.
 */
class UserTest {
    private static final String HASH = "PBKDF2WithHmacSHA512:210000:salt:hash";

    private final ConstraintViolations violations = new ConstraintViolations();

    @AfterEach
    void closeValidator() {
        violations.close();
    }

    @Test
    void accepts_username_of_3_characters() {
        assertThat(violations.of(new User(null, "a_1", HASH, null))).isEmpty();
    }

    @Test
    void accepts_username_of_64_characters() {
        assertThat(violations.of(new User(null, "u".repeat(64), HASH, null))).isEmpty();
    }

    @Test
    void rejects_username_of_2_characters() {
        assertThat(violations.of(new User(null, "ab", HASH, null))).containsExactly("username Pattern");
    }

    @Test
    void rejects_username_of_65_characters() {
        assertThat(violations.of(new User(null, "u".repeat(65), HASH, null)))
                .containsExactly("username Pattern");
    }

    @Test
    void rejects_username_with_non_latin_letters() {
        assertThat(violations.of(new User(null, "иван", HASH, null))).containsExactly("username Pattern");
    }

    @Test
    void rejects_missing_username() {
        assertThat(violations.of(new User(null, null, HASH, null))).containsExactly("username NotNull");
    }

    @Test
    void rejects_blank_password_hash() {
        assertThat(violations.of(new User(null, "alice", " ", null)))
                .containsExactly("passwordHash NotBlank");
    }
}
