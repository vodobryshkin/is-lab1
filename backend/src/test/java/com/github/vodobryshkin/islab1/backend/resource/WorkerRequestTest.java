package com.github.vodobryshkin.islab1.backend.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.github.vodobryshkin.islab1.backend.domain.Status;
import com.github.vodobryshkin.islab1.backend.domain.Worker;
import com.github.vodobryshkin.islab1.backend.dto.ProblemDetails;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.ws.rs.WebApplicationException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Проверяет чтение работника из JSON по контракту: формы создания и PUT, новые объекты
 * и ссылки, nullable-поля, зарплату float64, шаблон имени и серверные поля.
 */
class WorkerRequestTest {
    private static final String FIELDS =
            """
            "name": "Иван Иванов", "salary": 100000.5, "rating": 7,
            "start_date": "2026-10-06T12:00:00Z", "position": null, "status": "HIRED"
            """;
    private static final String NEW_COORDINATES = "\"coordinates\": {\"x\": -953, \"y\": -391}";
    private static final String NEW_PERSON =
            """
            "person": {"eye_color": null, "hair_color": "BLACK",
              "location": {"x": 1, "y": 2, "z": 3}, "birthday": "2000-01-01T00:00:00Z",
              "weight": 70.5, "nationality": "RUSSIA"}
            """.strip();
    private static final String BASE = FIELDS + ", " + NEW_COORDINATES + ", " + NEW_PERSON;
    private static final String NEW_ORGANIZATION =
            """
            "organization": {"full_name": "ООО Ромашка", "annual_turnover": 1.5,
              "employees_count": 3, "rating": 5, "type": null,
              "official_address": {"street": "Kronverksky", "town": null},
              "postal_address_id": 20}
            """.strip();

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final RequestReader reader = new RequestReader(factory.getValidator());
    private final RequestEntities entities = new RequestEntities();

    @AfterEach
    void closeFactory() {
        factory.close();
    }

    @Test
    void reads_worker_without_organization_with_new_coordinates_and_person() {
        // Act
        Worker actual = create(BASE);

        // Assert
        assertAll(
                () -> assertThat(actual.getName()).isEqualTo("Иван Иванов"),
                () -> assertThat(actual.getSalary()).isEqualTo(100000.5),
                () -> assertThat(actual.getStatus()).isEqualTo(Status.HIRED),
                () -> assertThat(actual.getPosition()).isNull(),
                () -> assertThat(actual.getCoordinates().getX()).isEqualTo(-953),
                () -> assertThat(actual.getPerson().getId()).isNull(),
                () -> assertThat(actual.getPerson().getLocation().getZ()).isEqualTo(3L),
                () -> assertThat(actual.getOrganization()).isNull(),
                () -> assertThat(actual.getUser()).isNull(),
                () -> assertThat(actual.getCreationDate()).isNull()
        );
    }

    @Test
    void reads_existing_coordinates_person_and_organization() {
        // Act
        Worker actual = create(FIELDS + ", \"coordinates\": {\"id\": 5}, \"person\": {\"id\": 10}, "
                + "\"organization_id\": 1");

        // Assert
        assertAll(
                () -> assertThat(actual.getCoordinates().getId()).isEqualTo(5L),
                () -> assertThat(actual.getPerson().getId()).isEqualTo(10L),
                () -> assertThat(actual.getOrganization().getId()).isEqualTo(1L)
        );
    }

    @Test
    void reads_new_organization_with_address_variants() {
        // Act
        Worker actual = create(BASE + ", " + NEW_ORGANIZATION);

        // Assert
        assertAll(
                () -> assertThat(actual.getOrganization().getId()).isNull(),
                () -> assertThat(actual.getOrganization().getFullName()).isEqualTo("ООО Ромашка"),
                () -> assertThat(actual.getOrganization().getOfficialAddress().getStreet())
                        .isEqualTo("Kronverksky"),
                () -> assertThat(actual.getOrganization().getPostalAddress().getId()).isEqualTo(20L)
        );
    }

    @Test
    void accepts_null_person() {
        // Act
        Worker actual = create(FIELDS + ", " + NEW_COORDINATES + ", \"person\": null");

        // Assert
        assertThat(actual.getPerson()).isNull();
    }

    @Test
    void reads_update_without_organization() {
        // Act
        Worker actual = update(BASE + ", \"organization_id\": null");

        // Assert
        assertThat(actual.getOrganization()).isNull();
    }

    @Test
    void rejects_existing_and_new_organization_together() {
        // Act
        List<String> actual = createIssues(BASE + ", \"organization_id\": 1, " + NEW_ORGANIZATION);

        // Assert
        assertThat(actual).containsExactlyInAnyOrder("organization_id MUTUALLY_EXCLUSIVE",
                "organization MUTUALLY_EXCLUSIVE");
    }

    @Test
    void rejects_null_organization_and_non_positive_organization_id() {
        // Act
        List<String> withNull = createIssues(BASE + ", \"organization\": null");
        List<String> withZero = createIssues(BASE + ", \"organization_id\": 0");

        // Assert
        assertAll(
                () -> assertThat(withNull).containsExactly("organization MUST_NOT_BE_NULL"),
                () -> assertThat(withZero).containsExactly("organization_id MUST_BE_POSITIVE")
        );
    }

    @Test
    void requires_organization_id_on_update_and_rejects_new_organization() {
        // Act
        List<String> actual = issues(BASE + ", " + NEW_ORGANIZATION, true);

        // Assert
        assertThat(actual).containsExactlyInAnyOrder("organization UNKNOWN_FIELD",
                "organization_id REQUIRED");
    }

    @Test
    void rejects_missing_coordinates_and_mixed_person_reference() {
        // Act
        List<String> actual = createIssues(FIELDS + ", \"coordinates\": null, "
                + "\"person\": {\"id\": 10, \"weight\": 1}");

        // Assert
        assertThat(actual).containsExactlyInAnyOrder("coordinates MUST_NOT_BE_NULL",
                "person.id MUTUALLY_EXCLUSIVE");
    }

    @Test
    void reports_nested_errors_with_contract_names() {
        // Act
        List<String> actual = createIssues(FIELDS
                + ", \"coordinates\": {\"x\": -954, \"y\": 0}, \"person\": {\"eye_color\": null, "
                + "\"hair_color\": \"BLACK\", \"location\": {\"x\": 1, \"y\": 2}, "
                + "\"birthday\": \"2000-01-01T00:00:00Z\", \"weight\": 0, \"nationality\": \"RUSSIA\"}, "
                + NEW_ORGANIZATION.replace("ООО Ромашка", " "));

        // Assert
        assertThat(actual).containsExactlyInAnyOrder("coordinates.x MUST_BE_GREATER_THAN_MINUS_954",
                "person.location.z REQUIRED", "person.weight MUST_BE_POSITIVE",
                "organization.full_name MUST_NOT_BE_EMPTY");
    }

    @Test
    void rejects_non_positive_salary_and_float64_overflow() {
        // Act
        List<String> zero = createIssues(BASE.replace("100000.5", "0"));
        List<String> overflow = createIssues(BASE.replace("100000.5", "1e309"));

        // Assert
        assertAll(
                () -> assertThat(zero).containsExactly("salary MUST_BE_POSITIVE"),
                () -> assertThat(overflow).containsExactly("salary OUT_OF_RANGE")
        );
    }

    @Test
    void checks_name_against_contract_pattern() {
        // Act
        List<String> symbols = createIssues(BASE.replace("Иван Иванов", "Иван!"));
        List<String> empty = createIssues(BASE.replace("Иван Иванов", ""));

        // Assert
        assertAll(
                () -> assertThat(symbols).containsExactly("name MUST_MATCH_PATTERN"),
                () -> assertThat(empty).containsExactly("name MUST_NOT_BE_EMPTY")
        );
    }

    @Test
    void requires_nullable_enums_and_checks_their_values() {
        // Act
        List<String> actual = createIssues(BASE.replace("\"position\": null, ", "")
                .replace("\"HIRED\"", "\"FIRED\""));

        // Assert
        assertThat(actual).containsExactlyInAnyOrder("position REQUIRED",
                "status INVALID_ENUM_VALUE");
    }

    @Test
    void rejects_server_generated_fields() {
        // Act
        List<String> actual = createIssues(BASE + ", \"id\": 1, \"creation_date\": "
                + "\"2026-01-01T00:00:00Z\", \"creator_id\": 1");

        // Assert
        assertThat(actual).containsExactlyInAnyOrder("id UNKNOWN_FIELD",
                "creation_date UNKNOWN_FIELD", "creator_id UNKNOWN_FIELD");
    }

    private Worker create(String fields) {
        return read(fields, false);
    }

    private Worker update(String fields) {
        return read(fields, true);
    }

    private Worker read(String fields, boolean update) {
        RequestFields body = reader.read("{" + fields + "}");
        return body.validate(entities.worker(body, update), "user", "creation_date");
    }

    private List<String> createIssues(String fields) {
        return issues(fields, false);
    }

    private List<String> issues(String fields, boolean update) {
        WebApplicationException exception =
                catchThrowableOfType(WebApplicationException.class, () -> read(fields, update));
        ProblemDetails problem = (ProblemDetails) exception.getResponse().getEntity();
        return problem.getErrors().stream()
                .map(issue -> issue.getField() + " " + issue.getTitle())
                .toList();
    }
}
