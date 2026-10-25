package com.github.vodobryshkin.islab1.backend.resource;

import com.github.vodobryshkin.islab1.backend.dto.CredentialsRequest;
import com.github.vodobryshkin.islab1.backend.dto.ProblemDetails;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.WebApplicationException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Читает логин и пароль из JSON запросов регистрации и входа.
 *
 * <p>JSON-P различает отсутствие поля, {@code null} и неверный тип, которые после привязки
 * к DTO выглядят одинаково. Правила содержимого проверяет Jakarta Validation; для каждого
 * поля в ответ попадает одно, самое общее нарушение.
 */
@ApplicationScoped
public class CredentialsReader {
    private static final int UNPROCESSABLE_CONTENT = 422;
    private static final List<String> CONSTRAINTS =
            List.of("NotNull", "NotEmpty", "Size", "Pattern");
    private static final List<String> CONSTRAINT_TITLES = List.of(
            "MUST_NOT_BE_NULL", "MUST_NOT_BE_EMPTY", "OUT_OF_RANGE", "MUST_MATCH_PATTERN");

    private final Validator validator;

    /**
     * Создаёт чтение запросов.
     *
     * @param validator Jakarta Validation
     */
    @Inject
    public CredentialsReader(Validator validator) {
        this.validator = validator;
    }

    /** Нужен CDI для создания клиентского прокси; прикладной код его не вызывает. */
    protected CredentialsReader() {
        this.validator = null;
    }

    /**
     * Читает логин и пароль из тела запроса.
     *
     * @param body тело запроса
     * @return проверенные логин и пароль
     * @throws WebApplicationException с готовым ответом 400 или 422, если данные неверны
     */
    public CredentialsRequest read(String body) {
        JsonObject json;
        try (JsonReader reader = Json.createReader(new StringReader(body))) {
            json = reader.readObject();
        } catch (JsonException exception) {
            throw new WebApplicationException(exception, new ProblemDetails(400,
                    "/problems/malformed-json", "Malformed JSON",
                    "The request body contains invalid JSON.").toResponse());
        }
        List<ProblemDetails.Issue> issues = new ArrayList<>();
        CredentialsRequest credentials = new CredentialsRequest(
                readString(json, "username", issues), readString(json, "password", issues));
        if (issues.isEmpty()) {
            validator.validate(credentials).stream()
                    .collect(Collectors.groupingBy(
                            violation -> violation.getPropertyPath().toString(), TreeMap::new,
                            Collectors.minBy(Comparator.comparingInt(this::orderOf))))
                    .values()
                    .stream()
                    .flatMap(Optional::stream)
                    .map(violation -> new ProblemDetails.Issue(
                            violation.getPropertyPath().toString(),
                            CONSTRAINT_TITLES.get(orderOf(violation)), violation.getMessage()))
                    .forEach(issues::add);
        }
        if (!issues.isEmpty()) {
            throw new WebApplicationException(new ProblemDetails("/problems/validation-failed",
                    "Validation failed", UNPROCESSABLE_CONTENT, null, issues).toResponse());
        }
        return credentials;
    }

    private String readString(JsonObject json, String field, List<ProblemDetails.Issue> issues) {
        JsonValue value = json.get(field);
        if (value == null) {
            issues.add(new ProblemDetails.Issue(field, "REQUIRED",
                    "Field " + field + " is required."));
        } else if (value.getValueType() == JsonValue.ValueType.NULL) {
            issues.add(new ProblemDetails.Issue(field, "MUST_NOT_BE_NULL",
                    "Field " + field + " must not be null."));
        } else if (value instanceof JsonString string) {
            return string.getString();
        } else {
            issues.add(new ProblemDetails.Issue(field, "INVALID_TYPE",
                    "Field " + field + " must be a string."));
        }
        return null;
    }

    private int orderOf(ConstraintViolation<?> violation) {
        String constraint = violation.getConstraintDescriptor().getAnnotation()
                .annotationType().getSimpleName();
        return Math.max(0, CONSTRAINTS.indexOf(constraint));
    }
}
