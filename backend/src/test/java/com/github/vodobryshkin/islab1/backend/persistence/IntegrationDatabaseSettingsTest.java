package com.github.vodobryshkin.islab1.backend.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Проверяет защиту от подключения тестов к рабочей БД.
 */
class IntegrationDatabaseSettingsTest {
    private static final String URL_VARIABLE = "IS_LAB1_TEST_DB_URL";

    @Test
    void uses_in_memory_h2_by_default() {
        // Act
        IntegrationDatabaseSettings sut = new IntegrationDatabaseSettings(Map.of());

        // Assert
        assertAll(
                () -> assertThat(sut.isH2()).isTrue(),
                () -> assertThat(sut.getUrl()).startsWith("jdbc:h2:mem:islab1_test;")
        );
    }

    @Test
    void accepts_postgresql_database_with_test_suffix() {
        // Act
        IntegrationDatabaseSettings sut = new IntegrationDatabaseSettings(
                Map.of(URL_VARIABLE, "jdbc:postgresql://localhost:5432/is_lab1_test"));

        // Assert
        assertThat(sut.isH2()).isFalse();
    }

    @Test
    void rejects_department_working_database() {
        // Act & Assert
        assertThatThrownBy(() -> new IntegrationDatabaseSettings(
                Map.of(URL_VARIABLE, "jdbc:postgresql://pg:5432/studs")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("studs");
    }

    @Test
    void rejects_database_which_only_contains_test_in_name() {
        // Act & Assert
        assertThatThrownBy(() -> new IntegrationDatabaseSettings(
                Map.of(URL_VARIABLE, "jdbc:postgresql://localhost:5432/is_lab1_test_copy")))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void rejects_unsupported_database_url() {
        // Act & Assert
        assertThatThrownBy(() -> new IntegrationDatabaseSettings(
                Map.of(URL_VARIABLE, "jdbc:h2:file:/tmp/islab1_test")))
                .isInstanceOf(IllegalStateException.class);
    }
}
