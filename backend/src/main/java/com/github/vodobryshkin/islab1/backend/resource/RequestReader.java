package com.github.vodobryshkin.islab1.backend.resource;

import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import com.github.vodobryshkin.islab1.backend.dto.ProblemDetails;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Читает тело, ID пути и параметры списка и переводит ошибки в коды контракта.
 *
 * <p>Тело разбирается через JSON-P: так различаются отсутствие поля, {@code null},
 * неверный тип и выход за диапазон типа, которые после привязки к объекту неотличимы.
 * Правила значений проверяет Jakarta Validation. Ошибка сразу завершает запрос
 * ответом 400 или 422 через {@link WebApplicationException} с готовым ответом.
 */
@ApplicationScoped
public class RequestReader {
    private static final int UNPROCESSABLE_CONTENT = 422;
    private static final String BODY = "$";
    private static final int MAX_PAGE_SIZE = 100;
    private static final String OUT_OF_RANGE = "OUT_OF_RANGE";
    private static final String FIELD = "Field ";

    /** Для каждого поля в ответ попадает первое нарушение в этом порядке. */
    private static final List<String> CONSTRAINTS = List.of("NotNull", "NotEmpty", "NotBlank",
            "Size", "Min", "Positive", "DecimalMax", "Pattern");

    private final Validator validator;

    /**
     * Создаёт чтение запросов.
     *
     * @param validator Jakarta Validation
     */
    @Inject
    public RequestReader(Validator validator) {
        this.validator = validator;
    }

    /** Нужен CDI для создания клиентского прокси; прикладной код его не вызывает. */
    protected RequestReader() {
        this.validator = null;
    }

    /**
     * Разбирает тело запроса как JSON-объект.
     *
     * @param body тело запроса
     * @return поля тела
     * @throws WebApplicationException 400, если JSON некорректен; 422, если это не объект
     */
    public Fields read(String body) {
        if (body == null || body.isBlank()) {
            throw invalid(List.of(issue(BODY, "REQUIRED", "Request body is required.")));
        }
        JsonValue json;
        try (JsonReader reader = Json.createReader(new StringReader(body))) {
            json = reader.readValue();
        } catch (JsonException exception) {
            throw new WebApplicationException(exception, new ProblemDetails(400,
                    "/problems/malformed-json", "Malformed JSON",
                    "The request body contains invalid JSON.").toResponse());
        }
        if (json.getValueType() == JsonValue.ValueType.NULL) {
            throw invalid(List.of(issue(BODY, "MUST_NOT_BE_NULL",
                    "Request body must not be null.")));
        }
        if (!(json instanceof JsonObject object)) {
            throw invalid(List.of(issue(BODY, "INVALID_TYPE",
                    "Request body must be a JSON object.")));
        }
        return new Fields(object);
    }

    /**
     * Проверяет ID из пути.
     *
     * @param raw значение из пути
     * @return положительный ID
     * @throws WebApplicationException 400 с нарушением параметра {@code id}
     */
    public long id(String raw) {
        List<ProblemDetails.Issue> issues = new ArrayList<>();
        Long id = positiveLong(raw, "id", issues);
        if (!issues.isEmpty()) {
            throw invalidParameters(issues);
        }
        return id;
    }

    /**
     * Проверяет параметры списка {@code page}, {@code page_size}, {@code sort_by} и
     * {@code sort_order}; отсутствующий параметр получает значение по умолчанию.
     *
     * @param parameters параметры запроса
     * @param sortFields разрешённые поля сортировки ресурса
     * @return проверенные параметры
     * @throws WebApplicationException 400 со всеми нарушениями параметров
     */
    public ListQuery listQuery(MultivaluedMap<String, String> parameters, Set<String> sortFields) {
        List<ProblemDetails.Issue> issues = new ArrayList<>();
        long page = number(parameters, "page", 1, Integer.MAX_VALUE, issues);
        long pageSize = number(parameters, "page_size", 20, MAX_PAGE_SIZE, issues);
        String sortBy = choice(parameters, "sort_by", "id", sortFields, issues);
        String sortOrder = choice(parameters, "sort_order", "asc", Set.of("asc", "desc"), issues);
        if (!issues.isEmpty()) {
            throw invalidParameters(issues);
        }
        return new ListQuery((int) page, (int) pageSize, sortBy, "asc".equals(sortOrder));
    }

    private long number(MultivaluedMap<String, String> parameters, String name, long fallback,
            long max, List<ProblemDetails.Issue> issues) {
        String raw = parameters.getFirst(name);
        Long value = raw == null ? Long.valueOf(fallback) : positiveLong(raw, name, issues);
        if (value != null && value > max) {
            issues.add(issue(name, OUT_OF_RANGE, name + " must not exceed " + max + "."));
        }
        return value == null ? fallback : value;
    }

    private String choice(MultivaluedMap<String, String> parameters, String name, String fallback,
            Set<String> allowed, List<ProblemDetails.Issue> issues) {
        String value = parameters.getFirst(name);
        if (value == null) {
            return fallback;
        }
        if (!allowed.contains(value)) {
            issues.add(issue(name, "INVALID_ENUM_VALUE",
                    name + " must be one of " + new TreeSet<>(allowed) + "."));
        }
        return value;
    }

    private Long positiveLong(String raw, String name, List<ProblemDetails.Issue> issues) {
        if (!raw.matches("-?\\d+")) {
            issues.add(issue(name, "INVALID_TYPE", name + " must be an integer."));
            return null;
        }
        long value;
        try {
            value = Long.parseLong(raw);
        } catch (NumberFormatException exception) {
            issues.add(issue(name, OUT_OF_RANGE, name + " is too large."));
            return null;
        }
        if (value <= 0) {
            issues.add(issue(name, "MUST_BE_POSITIVE", name + " must be greater than zero."));
            return null;
        }
        return value;
    }

    private WebApplicationException invalidParameters(List<ProblemDetails.Issue> issues) {
        return new WebApplicationException(new ProblemDetails("/problems/validation-failed",
                "Invalid request parameters", 400, null, issues).toResponse());
    }

    private WebApplicationException invalid(List<ProblemDetails.Issue> issues) {
        return new WebApplicationException(new ProblemDetails("/problems/validation-failed",
                "Validation failed", UNPROCESSABLE_CONTENT, null, issues).toResponse());
    }

    private ProblemDetails.Issue issue(String field, String title, String description) {
        return new ProblemDetails.Issue(field, title, description);
    }

    /**
     * Поля одного JSON-объекта тела; накапливает нарушения до вызова {@link #validate}.
     */
    public final class Fields {
        private final JsonObject json;
        private final List<ProblemDetails.Issue> issues = new ArrayList<>();

        private Fields(JsonObject json) {
            this.json = json;
        }

        /**
         * Отмечает поля, которых нет в контракте.
         *
         * @param allowed имена полей контракта
         * @return эти же поля
         */
        public Fields allowOnly(String... allowed) {
            Set<String> names = Set.of(allowed);
            json.keySet().stream().filter(name -> !names.contains(name)).sorted()
                    .forEach(name -> issues.add(issue(name, "UNKNOWN_FIELD",
                            FIELD + name + " is not supported.")));
            return this;
        }

        /**
         * Читает строку.
         *
         * @param field имя поля
         * @return значение или {@code null}, если поле неверно
         */
        public String string(String field) {
            JsonValue value = present(field);
            if (value instanceof JsonString string) {
                return string.getString();
            }
            invalidType(value, field, "a string");
            return null;
        }

        /**
         * Читает целое число типа int32.
         *
         * @param field имя поля
         * @return значение или {@code null}, если поле неверно
         */
        public Integer int32(String field) {
            Long value = integer(field);
            if (value != null && (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE)) {
                issues.add(issue(field, OUT_OF_RANGE, field + " must fit into int32."));
                return null;
            }
            return value == null ? null : value.intValue();
        }

        /**
         * Читает целое число типа int64.
         *
         * @param field имя поля
         * @return значение или {@code null}, если поле неверно
         */
        public Long int64(String field) {
            return integer(field);
        }

        /**
         * Проверяет собранный объект через Jakarta Validation; поля с ошибкой структуры
         * в ответе представлены только ею.
         *
         * @param value объект из прочитанных полей
         * @param <T> тип объекта
         * @return тот же объект
         * @throws WebApplicationException 422 со всеми нарушениями полей
         */
        public <T> T validate(T value) {
            Set<String> invalidFields = issues.stream().map(ProblemDetails.Issue::getField)
                    .collect(Collectors.toSet());
            validator.validate(value).stream()
                    .filter(violation -> !invalidFields.contains(
                            violation.getPropertyPath().toString()))
                    .collect(Collectors.groupingBy(violation -> violation.getPropertyPath()
                                    .toString(), TreeMap::new,
                            Collectors.minBy(Comparator.comparingInt(this::orderOf))))
                    .values()
                    .stream()
                    .flatMap(Optional::stream)
                    .map(this::toIssue)
                    .forEach(issues::add);
            if (!issues.isEmpty()) {
                throw invalid(issues);
            }
            return value;
        }

        private Long integer(String field) {
            JsonValue value = present(field);
            if (!(value instanceof JsonNumber number) || !number.isIntegral()) {
                invalidType(value, field, "an integer");
                return null;
            }
            try {
                return number.longValueExact();
            } catch (ArithmeticException exception) {
                issues.add(issue(field, OUT_OF_RANGE, field + " must fit into int64."));
                return null;
            }
        }

        private JsonValue present(String field) {
            JsonValue value = json.get(field);
            if (value == null) {
                issues.add(issue(field, "REQUIRED", FIELD + field + " is required."));
            } else if (value.getValueType() == JsonValue.ValueType.NULL) {
                issues.add(issue(field, "MUST_NOT_BE_NULL",
                        FIELD + field + " must not be null."));
            }
            return value;
        }

        private void invalidType(JsonValue value, String field, String expected) {
            if (value != null && value.getValueType() != JsonValue.ValueType.NULL) {
                issues.add(issue(field, "INVALID_TYPE",
                        FIELD + field + " must be " + expected + "."));
            }
        }

        private int orderOf(ConstraintViolation<?> violation) {
            return CONSTRAINTS.indexOf(constraintOf(violation));
        }

        private ProblemDetails.Issue toIssue(ConstraintViolation<?> violation) {
            String field = violation.getPropertyPath().toString();
            boolean defaultMessage =
                    violation.getConstraintDescriptor().getMessageTemplate().startsWith("{");
            String custom = defaultMessage ? null : violation.getMessage();
            return switch (constraintOf(violation)) {
                case "NotNull" -> violationIssue(field, "MUST_NOT_BE_NULL", custom,
                        field + " must not be null.");
                case "NotEmpty", "NotBlank" -> violationIssue(field, "MUST_NOT_BE_EMPTY", custom,
                        field + " must not be empty.");
                case "Positive" -> violationIssue(field, "MUST_BE_POSITIVE", custom,
                        field + " must be greater than zero.");
                case "Pattern" -> violationIssue(field, "MUST_MATCH_PATTERN", custom,
                        field + " has an invalid format.");
                case "Min" -> violationIssue(field, "MUST_BE_GREATER_THAN_" + boundName(violation),
                        custom, field + " must be greater than " + bound(violation) + ".");
                default -> violationIssue(field, OUT_OF_RANGE, custom, field + " is out of range.");
            };
        }

        private ProblemDetails.Issue violationIssue(String field, String title, String custom,
                String description) {
            return new ProblemDetails.Issue(field, title, custom == null ? description : custom);
        }

        /** {@code @Min(-953)} означает «больше -954», как в ТЗ и контракте. */
        private long bound(ConstraintViolation<?> violation) {
            return ((Min) violation.getConstraintDescriptor().getAnnotation()).value() - 1;
        }

        private String boundName(ConstraintViolation<?> violation) {
            long bound = bound(violation);
            return bound < 0 ? "MINUS_" + -bound : String.valueOf(bound);
        }

        private String constraintOf(ConstraintViolation<?> violation) {
            return violation.getConstraintDescriptor().getAnnotation().annotationType()
                    .getSimpleName();
        }
    }
}
