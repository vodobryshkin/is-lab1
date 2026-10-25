package com.github.vodobryshkin.islab1.backend.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Выделенная тестовая БД для JUnit вне контейнера.
 *
 * <p>Создаёт схему рабочим скриптом {@code db/schema.sql}, открывает фабрику unit
 * {@code is-lab1-test} с транзакциями {@code RESOURCE_LOCAL} и очищает таблицы перед тестом.
 * Для H2 перед рабочим скриптом выполняется {@code db/h2-compatibility.sql}: он добавляет
 * тип {@code timestamptz} и именованные последовательности, которые PostgreSQL создаёт
 * для {@code BIGSERIAL} сам. Поэтому проверка на H2 не заменяет запуск на PostgreSQL.
 */
final class IntegrationDatabase implements AutoCloseable {
    private static final String PERSISTENCE_UNIT = "is-lab1-test";
    private static final String SCHEMA_PROPERTY = "islab1.schema";
    private static final String H2_COMPATIBILITY_SCRIPT = "/db/h2-compatibility.sql";

    /** Порядок удаления: сначала таблицы, которые ссылаются на остальные. */
    private static final List<String> TABLES_IN_DELETE_ORDER = List.of(
            "persons",
            "workers",
            "organizations",
            "addresses",
            "locations",
            "coordinates",
            "users"
    );

    private final EntityManagerFactory factory;

    IntegrationDatabase() {
        this(new IntegrationDatabaseSettings(System.getenv()));
    }

    IntegrationDatabase(IntegrationDatabaseSettings settings) {
        createSchema(settings);
        this.factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, Map.of(
                "jakarta.persistence.jdbc.url", settings.getUrl(),
                "jakarta.persistence.jdbc.user", settings.getUser(),
                "jakarta.persistence.jdbc.password", settings.getPassword()
        ));
    }

    EntityManager createEntityManager() {
        return factory.createEntityManager();
    }

    /** Возвращает ID сущности, назначенный при сохранении. */
    Object identifierOf(Object entity) {
        return factory.getPersistenceUnitUtil().getIdentifier(entity);
    }

    /**
     * Выполняет действие в отдельной транзакции и фиксирует её.
     * При исключении транзакция откатывается, а исключение передаётся дальше.
     */
    void inTransaction(Consumer<EntityManager> action) {
        try (EntityManager entityManager = createEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                action.accept(entityManager);
                transaction.commit();
            } finally {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
            }
        }
    }

    /** Удаляет строки всех таблиц с учётом внешних ключей. */
    void clean() {
        inTransaction(entityManager -> TABLES_IN_DELETE_ORDER.forEach(table ->
                entityManager.createNativeQuery("DELETE FROM " + table).executeUpdate()));
    }

    @Override
    public void close() {
        factory.close();
    }

    private void createSchema(IntegrationDatabaseSettings settings) {
        try (Connection connection = DriverManager.getConnection(
                settings.getUrl(), settings.getUser(), settings.getPassword());
                Statement statement = connection.createStatement()) {
            if (settings.isH2()) {
                execute(statement, readCompatibilityScript());
            }
            execute(statement, Files.readString(
                    Path.of(System.getProperty(SCHEMA_PROPERTY)), StandardCharsets.UTF_8));
        } catch (SQLException exception) {
            throw new IllegalStateException("Cannot create the test database schema.", exception);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private String readCompatibilityScript() throws IOException {
        try (InputStream script = getClass().getResourceAsStream(H2_COMPATIBILITY_SCRIPT)) {
            if (script == null) {
                throw new IOException("Missing " + H2_COMPATIBILITY_SCRIPT);
            }
            return new String(script.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void execute(Statement statement, String script) throws SQLException {
        for (String sql : script.split(";\\s*(\\R|$)")) {
            if (!sql.isBlank()) {
                statement.execute(sql);
            }
        }
    }
}
