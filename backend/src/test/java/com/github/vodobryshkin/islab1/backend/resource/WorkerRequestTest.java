package com.github.vodobryshkin.islab1.backend.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.github.vodobryshkin.islab1.backend.domain.Status;
import com.github.vodobryshkin.islab1.backend.domain.Worker;
import com.github.vodobryshkin.islab1.backend.dto.common.ValidationErrorResponse;
import com.github.vodobryshkin.islab1.backend.dto.worker.WorkerRequest;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.ws.rs.WebApplicationException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Проверяет чтение работника из JSON: новые объекты и ссылки, nullable-поля и имена нарушений.
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
        List<String> actual = issues(BASE + ", \"organization_id\": 1, " + NEW_ORGANIZATION);

        // Assert
        assertThat(actual).containsExactly("organization INVALID_COMBINATION");
    }

    @Test
    void rejects_non_positive_organization_id() {
        // Act
        List<String> actual = issues(BASE + ", \"organization_id\": 0");

        // Assert
        assertThat(actual).containsExactly("organization_id MUST_BE_POSITIVE");
    }

    @Test
    void ignores_new_organization_on_update() {
        // Act
        Worker actual = update(BASE + ", " + NEW_ORGANIZATION);

        // Assert
        assertThat(actual.getOrganization()).isNull();
    }

    @Test
    void ignores_fields_of_person_reference() {
        // Act
        List<String> actual = issues(FIELDS + ", \"coordinates\": null, "
                + "\"person\": {\"id\": 10, \"weight\": 1}");

        // Assert
        assertThat(actual).containsExactly("coordinates MUST_NOT_BE_NULL");
    }

    @Test
    void reports_nested_errors_with_contract_names() {
        // Act
        List<String> actual = issues(FIELDS
                + ", \"coordinates\": {\"x\": -954, \"y\": 0}, \"person\": {\"eye_color\": null, "
                + "\"hair_color\": \"BLACK\", \"location\": {\"x\": 1, \"y\": 2}, "
                + "\"birthday\": \"2000-01-01T00:00:00Z\", \"weight\": 0, \"nationality\": \"RUSSIA\"}, "
                + NEW_ORGANIZATION.replace("ООО Ромашка", " "));

        // Assert
        assertThat(actual).containsExactlyInAnyOrder("coordinates.x MUST_BE_GREATER_THAN_MINUS_954",
                "person.location.z MUST_NOT_BE_NULL", "person.weight MUST_BE_POSITIVE",
                "organization.full_name MUST_NOT_BE_EMPTY");
    }

    @Test
    void rejects_non_positive_salary_and_float64_overflow() {
        // Act
        List<String> zero = issues(BASE.replace("100000.5", "0"));
        List<String> overflow = issues(BASE.replace("100000.5", "1e309"));

        // Assert
        assertAll(
                () -> assertThat(zero).containsExactly("salary MUST_BE_POSITIVE"),
                () -> assertThat(overflow).containsExactly("salary OUT_OF_RANGE")
        );
    }

    @Test
    void checks_name_against_contract_pattern() {
        // Act
        List<String> symbols = issues(BASE.replace("Иван Иванов", "Иван!"));
        List<String> empty = issues(BASE.replace("Иван Иванов", ""));

        // Assert
        assertAll(
                () -> assertThat(symbols).containsExactly("name MUST_MATCH_PATTERN"),
                () -> assertThat(empty).containsExactly("name MUST_NOT_BE_EMPTY")
        );
    }

    @Test
    void rejects_unknown_status_with_400() {
        // Act
        WebApplicationException actual = catchThrowableOfType(WebApplicationException.class,
                () -> create(BASE.replace("\"HIRED\"", "\"FIRED\"")));

        // Assert
        assertThat(actual.getResponse().getStatus()).isEqualTo(400);
    }

    @Test
    void ignores_server_generated_fields() {
        // Act
        Worker actual = create(BASE + ", \"id\": 1, \"creation_date\": \"2026-01-01T00:00:00Z\", "
                + "\"creator_id\": 1");

        // Assert
        assertAll(
                () -> assertThat(actual.getId()).isNull(),
                () -> assertThat(actual.getCreationDate()).isNull(),
                () -> assertThat(actual.getUser()).isNull()
        );
    }

    private Worker create(String fields) {
        return reader.read("{" + fields + "}", WorkerRequest.class).toEntity();
    }

    private Worker update(String fields) {
        WorkerRequest request = reader.read("{" + fields + "}", WorkerRequest.class);
        request.setOrganization(null);

        return request.toEntity();
    }

    private List<String> issues(String fields) {
        WebApplicationException exception =
                catchThrowableOfType(WebApplicationException.class, () -> create(fields));
        ValidationErrorResponse response = (ValidationErrorResponse) exception.getResponse().getEntity();

        return response.getErrors().stream()
                .map(issue -> issue.getField() + " " + issue.getCode())
                .toList();
    }
}
