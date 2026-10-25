package com.github.vodobryshkin.islab1.backend.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.github.vodobryshkin.islab1.backend.domain.Address;
import com.github.vodobryshkin.islab1.backend.domain.Color;
import com.github.vodobryshkin.islab1.backend.domain.Coordinates;
import com.github.vodobryshkin.islab1.backend.domain.Country;
import com.github.vodobryshkin.islab1.backend.domain.Location;
import com.github.vodobryshkin.islab1.backend.domain.Organization;
import com.github.vodobryshkin.islab1.backend.domain.OrganizationType;
import com.github.vodobryshkin.islab1.backend.domain.Person;
import com.github.vodobryshkin.islab1.backend.domain.Position;
import com.github.vodobryshkin.islab1.backend.domain.Status;
import com.github.vodobryshkin.islab1.backend.domain.User;
import com.github.vodobryshkin.islab1.backend.domain.Worker;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * Проверяет настройку EclipseLink с тестовой БД: генерацию ID, сохранение,
 * откат транзакции, ограничения рабочей схемы и очистку таблиц.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersistenceSetupTest {
    private static final ZonedDateTime START_DATE =
            ZonedDateTime.parse("2026-01-02T03:04:05+03:00[Europe/Moscow]");
    private static final ZonedDateTime BIRTHDAY = ZonedDateTime.parse("2000-05-06T07:08:09Z");
    private static final String WORKER_ROW_QUERY =
            """
            SELECT w.name, w.startDate, w.creationDate, w.organization.fullName,
                   w.user.username, w.person.birthday
            FROM Worker w
            WHERE w.id = :id
            """;

    private IntegrationDatabase database;

    @BeforeAll
    void openDatabase() {
        database = new IntegrationDatabase();
    }

    @AfterAll
    void closeDatabase() {
        database.close();
    }

    @BeforeEach
    void cleanDatabase() {
        database.clean();
    }

    @Test
    void assigns_generated_positive_id_to_new_coordinates() {
        // Arrange
        Coordinates coordinates = new Coordinates(null, 10, 20L);

        // Act
        database.inTransaction(entityManager -> entityManager.persist(coordinates));
        Object actualId = idOf(coordinates);

        // Assert
        assertThat(actualId)
                .as("ID must be assigned by the database sequence.")
                .isInstanceOfSatisfying(Long.class, id -> assertThat(id).isPositive());
    }

    @Test
    void reads_saved_coordinates_from_database_in_new_persistence_context() {
        // Arrange
        Coordinates coordinates = new Coordinates(null, -953, -391L);
        database.inTransaction(entityManager -> entityManager.persist(coordinates));
        Object id = idOf(coordinates);

        try (EntityManager entityManager = database.createEntityManager()) {
            // Act
            Object[] actualRow = entityManager.createQuery(
                            "SELECT c.x, c.y FROM Coordinates c WHERE c.id = :id", Object[].class)
                    .setParameter("id", id)
                    .getSingleResult();

            // Assert
            assertThat(actualRow).containsExactly(-953, -391L);
        }
    }

    @Test
    void discards_coordinates_written_in_rolled_back_transaction() {
        // Arrange
        try (EntityManager entityManager = database.createEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(new Coordinates(null, 1, 1L));
            entityManager.flush();

            // Act
            transaction.rollback();
        }

        // Assert
        assertThat(count("Coordinates"))
                .as("Rolled back rows must not remain in the database.")
                .isZero();
    }

    @Test
    void rolls_back_whole_transaction_when_one_insert_fails() {
        // Act
        Throwable actualException = catchThrowable(() -> database.inTransaction(entityManager -> {
            entityManager.persist(new Coordinates(null, 1, 1L));
            entityManager.flush();
            entityManager.createNativeQuery("INSERT INTO coordinates (x, y) VALUES (-954, 0)")
                    .executeUpdate();
        }));

        // Assert
        assertAll(
                () -> assertThat(actualException).isInstanceOf(PersistenceException.class),
                () -> assertThat(count("Coordinates"))
                        .as("The successful insert must be rolled back together with the failed one.")
                        .isZero()
        );
    }

    @Test
    void rejects_coordinates_below_minimum_by_database_constraint() {
        // Act & Assert
        assertThatThrownBy(() -> database.inTransaction(entityManager -> entityManager
                .createNativeQuery("INSERT INTO coordinates (x, y) VALUES (-954, 0)")
                .executeUpdate()))
                .as("The CHECK constraint of the schema must reject x = -954.")
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    void stores_worker_with_related_objects_and_time_zone_dates() {
        // Arrange
        Worker worker = persistWorkerGraph("stores");
        Object id = idOf(worker);

        try (EntityManager entityManager = database.createEntityManager()) {
            // Act
            Object[] actualRow = entityManager.createQuery(WORKER_ROW_QUERY, Object[].class)
                    .setParameter("id", id)
                    .getSingleResult();

            // Assert
            assertAll(
                    () -> assertThat(actualRow[0]).isEqualTo("Worker stores"),
                    () -> assertThat(((ZonedDateTime) actualRow[1]).toInstant())
                            .isEqualTo(START_DATE.toInstant()),
                    () -> assertThat(actualRow[2])
                            .as("Creation date must be generated before insert.")
                            .isNotNull(),
                    () -> assertThat(actualRow[3]).isEqualTo("Organization stores"),
                    () -> assertThat(actualRow[4]).isEqualTo("user_stores"),
                    () -> assertThat(((ZonedDateTime) actualRow[5]).toInstant())
                            .isEqualTo(BIRTHDAY.toInstant())
            );
        }
    }

    @Test
    void removes_rows_linked_by_foreign_keys_on_cleaning() {
        // Arrange
        persistWorkerGraph("cleaning");

        // Act
        database.clean();

        // Assert
        List<String> entities = List.of(
                "Person", "Worker", "Organization", "Address", "Location", "Coordinates", "User");
        assertThat(entities)
                .allSatisfy(entity -> assertThat(count(entity)).as(entity).isZero());
    }

    private Worker persistWorkerGraph(String suffix) {
        User user = new User(null, "user_" + suffix, null);
        Coordinates coordinates = new Coordinates(null, 1, 2L);
        Location location = new Location(null, 1, 2, 3L);
        Address address = new Address(null, "Kronverksky " + suffix, location);
        Organization organization = new Organization(null, address, 10.5f, 3L,
                "Organization " + suffix, 5, OrganizationType.TRUST, address);
        Worker worker = new Worker(null, user, "Worker " + suffix, coordinates, null, organization,
                1000.25, 7, START_DATE, Position.DIRECTOR, Status.HIRED, null);
        Person person = new Person(null, null, Color.BLACK, location, BIRTHDAY, 70.5f,
                Country.RUSSIA, worker);

        database.inTransaction(entityManager -> List.of(
                user, coordinates, location, address, organization, worker, person)
                .forEach(entityManager::persist));
        return worker;
    }

    private Object idOf(Object entity) {
        return database.identifierOf(entity);
    }

    private long count(String entity) {
        try (EntityManager entityManager = database.createEntityManager()) {
            return entityManager.createQuery("SELECT COUNT(e) FROM " + entity + " e", Long.class)
                    .getSingleResult();
        }
    }
}
