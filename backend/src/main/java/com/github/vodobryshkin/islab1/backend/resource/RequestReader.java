package com.github.vodobryshkin.islab1.backend.resource;

import com.github.vodobryshkin.islab1.backend.dto.ErrorResponse;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import com.github.vodobryshkin.islab1.backend.dto.ValidationErrorResponse;
import com.github.vodobryshkin.islab1.backend.dto.ValidationIssue;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Читает тело, ID пути и параметры списка и отклоняет неверный запрос ответом 400 или 422.
 * Тело разбирается через JSON-P, чтобы различать отсутствие поля, null и неверный тип.
 */
@ApplicationScoped
public class RequestReader {
    private static final int MAX_PAGE_SIZE = 100;
    private static final Pattern SNAKE_CASE_BOUNDARY = Pattern.compile("([a-z0-9])([A-Z])");

    private static final Map<String, List<String>> RULES = Map.of(
            "NotNull", List.of("MUST_NOT_BE_NULL", " must not be null."),
            "NotEmpty", List.of("MUST_NOT_BE_EMPTY", " must not be empty."),
            "NotBlank", List.of("MUST_NOT_BE_EMPTY", " must not be empty."),
            "Positive", List.of("MUST_BE_POSITIVE", " must be greater than zero."),
            "Pattern", List.of("MUST_MATCH_PATTERN", " has an invalid format."));

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

    /**
     * Нужен CDI для создания клиентского прокси; прикладной код его не вызывает.
     */
    protected RequestReader() {
        this.validator = null;
    }

    /**
     * Разбирает тело запроса как JSON-объект.
     *
     * @param body тело запроса
     * @return поля тела
     */
    public RequestFields read(String body) {
        if (body == null || body.isBlank()) {
            throw invalid(List.of(
                    new ValidationIssue("$", "REQUIRED", "Request body is required.")));
        }
        JsonValue json;
        try (JsonReader reader = Json.createReader(new StringReader(body))) {
            json = reader.readValue();
        } catch (JsonException exception) {
            throw new WebApplicationException(exception,
                    badRequest("The request body contains invalid JSON.").getResponse());
        }
        if (json.getValueType() == JsonValue.ValueType.NULL) {
            throw invalid(List.of(new ValidationIssue("$", "MUST_NOT_BE_NULL",
                    "Request body must not be null.")));
        }
        if (!(json instanceof JsonObject object)) {
            throw invalid(List.of(new ValidationIssue("$", "INVALID_TYPE",
                    "Request body must be a JSON object.")));
        }
        return new RequestFields(this, object);
    }

    /**
     * Проверяет ID из пути.
     *
     * @param raw значение из пути
     * @return положительный ID
     */
    public long id(String raw) {
        return positive("id", raw, Long.MAX_VALUE);
    }

    /**
     * Проверяет ID работника из пути: в контракте это int32.
     *
     * @param raw значение из пути
     * @return положительный ID
     */
    public int workerId(String raw) {
        return (int) positive("id", raw, Integer.MAX_VALUE);
    }

    /**
     * Проверяет параметры списка; отсутствующий параметр получает значение по умолчанию.
     *
     * @param parameters параметры запроса
     * @param sortFields разрешённые поля сортировки записей
     * @return проверенные параметры
     */
    public ListQuery listQuery(MultivaluedMap<String, String> parameters, Set<String> sortFields) {
        String page = parameters.getFirst("page");
        String pageSize = parameters.getFirst("page_size");
        String sortBy = parameters.getFirst("sort_by");
        String sortOrder = parameters.getFirst("sort_order");
        if (sortBy != null && !sortFields.contains(sortBy)) {
            throw badRequest("Parameter sort_by has an unsupported value.");
        }
        if (sortOrder != null && !"asc".equals(sortOrder) && !"desc".equals(sortOrder)) {
            throw badRequest("Parameter sort_order must be asc or desc.");
        }
        return new ListQuery(
                page == null ? 1 : (int) positive("page", page, Integer.MAX_VALUE),
                pageSize == null ? 20 : (int) positive("page_size", pageSize, MAX_PAGE_SIZE),
                sortBy == null ? "id" : sortBy,
                !"desc".equals(sortOrder));
    }

    /**
     * Создаёт ошибку 400 с готовым ответом.
     *
     * @param message описание ошибки
     * @return исключение, которое завершает запрос
     */
    WebApplicationException badRequest(String message) {
        return new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new ErrorResponse(message))
                .build());
    }

    /**
     * Создаёт ошибку 422 со списком нарушений полей.
     *
     * @param issues нарушения
     * @return исключение, которое завершает запрос
     */
    WebApplicationException invalid(List<ValidationIssue> issues) {
        return new WebApplicationException(Response.status(422)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new ValidationErrorResponse(issues))
                .build());
    }

    /**
     * Проверяет объект через Jakarta Validation и переводит нарушения в коды контракта.
     *
     * @param value проверяемый объект
     * @return по одному нарушению на каждое неверное поле
     */
    List<ValidationIssue> violations(Object value) {
        return validator.validate(value).stream()
                .collect(Collectors.groupingBy(this::fieldOf,
                        Collectors.minBy(Comparator.comparingInt(this::orderOf))))
                .values()
                .stream()
                .flatMap(Optional::stream)
                .map(this::toIssue)
                .toList();
    }

    private long positive(String name, String raw, long max) {
        if (!raw.matches("-?\\d+")) {
            throw badRequest("Parameter %s must be an integer.".formatted(name));
        }
        BigInteger value = new BigInteger(raw);
        if (value.signum() <= 0) {
            throw badRequest("Parameter %s must be greater than zero.".formatted(name));
        }
        if (value.compareTo(BigInteger.valueOf(max)) > 0) {
            throw badRequest("Parameter %s must not exceed %d.".formatted(name, max));
        }
        return value.longValue();
    }

    private int orderOf(ConstraintViolation<?> violation) {
        return CONSTRAINTS.indexOf(constraintOf(violation));
    }

    /**
     * Путь свойств Java переводится в snake_case контракта, например official_address.town.x.
     */
    private String fieldOf(ConstraintViolation<?> violation) {
        return SNAKE_CASE_BOUNDARY.matcher(violation.getPropertyPath().toString())
                .replaceAll("$1_$2").toLowerCase(Locale.ROOT);
    }

    private ValidationIssue toIssue(ConstraintViolation<?> violation) {
        String field = fieldOf(violation);
        String constraint = constraintOf(violation);
        List<String> rule =
                RULES.getOrDefault(constraint, List.of("OUT_OF_RANGE", " is out of range."));
        String code = "Min".equals(constraint)
                ? "MUST_BE_GREATER_THAN_" + boundName(violation)
                : rule.get(0);
        String message = "Min".equals(constraint)
                ? field + " must be greater than " + bound(violation) + "."
                : field + rule.get(1);
        boolean defaultMessage =
                violation.getConstraintDescriptor().getMessageTemplate().startsWith("{");
        return new ValidationIssue(field, code, defaultMessage ? message : violation.getMessage());
    }

    /**
     * Граница из ТЗ: ограничение Min(-953) означает «больше -954».
     */
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
