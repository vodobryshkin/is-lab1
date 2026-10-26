package com.github.vodobryshkin.islab1.backend.resource;

import com.github.vodobryshkin.islab1.backend.domain.Location;
import com.github.vodobryshkin.islab1.backend.dto.ProblemDetails;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.ws.rs.WebApplicationException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Поля одного JSON-объекта тела запроса. Накапливает нарушения структуры
 * до {@link #validate}, который добавляет нарушения Jakarta Validation и завершает
 * запрос ответом 422, если что-то неверно. Нарушения вложенных объектов получают
 * имя с префиксом, например {@code location.x}.
 *
 * <p>PMD считает класс слишком большим (GodClass). Все методы работают с общими
 * объектом JSON, префиксом, списком нарушений и набором ссылок; разделение по типам
 * значений разнесло бы это состояние по двум классам и не упростило бы код.
 */
@SuppressWarnings("PMD.GodClass")
public final class RequestFields {
    private static final String FIELD = "Field ";
    private static final String INVALID_TYPE = "INVALID_TYPE";
    private static final String OUT_OF_RANGE = "OUT_OF_RANGE";
    private static final String ID_FIELD = "id";

    private final RequestReader reader;
    private final JsonObject json;
    private final String prefix;
    private final List<ProblemDetails.Issue> issues;

    /** Префиксы вложенных ссылок {@code {id}}: их поля нового объекта не проверяются. */
    private final Set<String> references;

    RequestFields(RequestReader reader, JsonObject json) {
        this(reader, json, "", new ArrayList<>(), new HashSet<>());
    }

    private RequestFields(RequestReader reader, JsonObject json, String prefix,
            List<ProblemDetails.Issue> issues, Set<String> references) {
        this.reader = reader;
        this.json = json;
        this.prefix = prefix;
        this.issues = issues;
        this.references = references;
    }

    /**
     * Отмечает поля, которых нет в контракте.
     *
     * @param allowed имена полей контракта
     * @return эти же поля
     */
    public RequestFields allowOnly(String... allowed) {
        Set<String> names = Set.of(allowed);
        json.keySet().stream().filter(key -> !names.contains(key)).sorted()
                .forEach(key -> add(key, "UNKNOWN_FIELD",
                        FIELD + name(key) + " is not supported."));
        return this;
    }

    /**
     * Читает вложенный объект.
     *
     * @param field имя поля
     * @param nullable допускается ли {@code null}
     * @return поля вложенного объекта или {@code null}, если значение null или неверно
     */
    public RequestFields object(String field, boolean nullable) {
        JsonValue value = json.get(field);
        if (value instanceof JsonObject object) {
            return new RequestFields(reader, object, name(field) + ".", issues, references);
        }
        if (value == null || !nullable && isNull(value)) {
            present(field);
        } else if (!isNull(value)) {
            add(field, INVALID_TYPE, FIELD + name(field) + " must be an object.");
        }
        return null;
    }

    /**
     * Различает варианты вложенного объекта «ссылка {@code {id}}» и «новый объект».
     * У ссылки поля нового объекта запрещены ({@code MUTUALLY_EXCLUSIVE}) и не проверяются.
     *
     * @param newObjectFields поля варианта «новый объект»
     * @return {@code true}, если это ссылка
     */
    private boolean isReference(String... newObjectFields) {
        List<String> allowed = new ArrayList<>(List.of(newObjectFields));
        allowed.add(ID_FIELD);
        allowOnly(allowed.toArray(String[]::new));
        if (!json.containsKey(ID_FIELD)) {
            return false;
        }
        if (List.of(newObjectFields).stream().anyMatch(json::containsKey)) {
            add(ID_FIELD, "MUTUALLY_EXCLUSIVE", "Use either id or the fields of a new object.");
        }
        references.add(prefix);
        return true;
    }

    /**
     * Читает локацию: новую {@code {x, y, z}} или ссылку {@code {id}}. У ссылки
     * в результате задан только ID.
     *
     * @param field имя поля
     * @param nullable допускается ли {@code null}
     * @return локация или {@code null}
     */
    public Location location(String field, boolean nullable) {
        RequestFields location = object(field, nullable);
        if (location == null) {
            return null;
        }
        if (location.isReference("x", "y", "z")) {
            return new Location(location.id(ID_FIELD), null, null, null);
        }
        return new Location(null, location.int32("x"), location.int32("y"), location.int64("z"));
    }

    /**
     * Читает положительный ID существующего объекта.
     *
     * @param field имя поля
     * @return ID или {@code null}, если поле неверно
     */
    public Long id(String field) {
        Long value = int64(field);
        if (value != null && value <= 0) {
            add(field, "MUST_BE_POSITIVE", name(field) + " must be greater than zero.");
            return null;
        }
        return value;
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
     * Читает значение перечисления по имени константы.
     *
     * @param field имя поля
     * @param type тип перечисления
     * @param nullable допускается ли {@code null}
     * @param <E> тип перечисления
     * @return значение или {@code null}
     */
    public <E extends Enum<E>> E enumValue(String field, Class<E> type, boolean nullable) {
        if (nullable && json.containsKey(field) && isNull(json.get(field))) {
            return null;
        }
        String text = string(field);
        if (text == null) {
            return null;
        }
        try {
            return Enum.valueOf(type, text);
        } catch (IllegalArgumentException exception) {
            add(field, "INVALID_ENUM_VALUE", name(field) + " has an unsupported value.");
            return null;
        }
    }

    /**
     * Читает дату и время RFC 3339 со смещением, например {@code 2026-10-02T17:16:48Z}.
     *
     * @param field имя поля
     * @return значение или {@code null}, если поле неверно
     */
    public ZonedDateTime dateTime(String field) {
        String text = string(field);
        if (text == null) {
            return null;
        }
        try {
            return OffsetDateTime.parse(text).toZonedDateTime();
        } catch (DateTimeParseException exception) {
            add(field, "INVALID_DATE_TIME", name(field) + " must be an RFC 3339 date-time.");
            return null;
        }
    }

    /**
     * Читает число типа float32.
     *
     * @param field имя поля
     * @return значение или {@code null}, если поле неверно
     */
    public Float float32(String field) {
        JsonValue value = present(field);
        if (!(value instanceof JsonNumber number)) {
            invalidType(value, field, "a number");
            return null;
        }
        BigDecimal decimal = number.bigDecimalValue();
        if (decimal.abs().compareTo(BigDecimal.valueOf(Float.MAX_VALUE)) > 0) {
            add(field, OUT_OF_RANGE, name(field) + " must fit into float32.");
            return null;
        }
        return decimal.floatValue();
    }

    /**
     * Читает целое число типа int32.
     *
     * @param field имя поля
     * @return значение или {@code null}, если поле неверно
     */
    public Integer int32(String field) {
        Long value = int64(field);
        if (value != null && (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE)) {
            add(field, OUT_OF_RANGE, name(field) + " must fit into int32.");
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
        JsonValue value = present(field);
        if (!(value instanceof JsonNumber number) || !number.isIntegral()) {
            invalidType(value, field, "an integer");
            return null;
        }
        try {
            return number.longValueExact();
        } catch (ArithmeticException exception) {
            add(field, OUT_OF_RANGE, name(field) + " must fit into int64.");
            return null;
        }
    }

    /**
     * Проверяет собранный объект через Jakarta Validation. Поля с ошибкой структуры
     * представлены только ею; поля ссылок {@code {id}} не проверяются как новый объект.
     *
     * @param value объект из прочитанных полей
     * @param <T> тип объекта
     * @return тот же объект
     * @throws WebApplicationException 422 со всеми нарушениями полей
     */
    public <T> T validate(T value) {
        Set<String> invalidFields = new HashSet<>();
        issues.forEach(issue -> invalidFields.add(issue.getField()));
        reader.violations(value).stream()
                .filter(issue -> !invalidFields.contains(issue.getField()))
                .filter(issue -> references.stream().noneMatch(issue.getField()::startsWith))
                .forEach(issues::add);
        if (!issues.isEmpty()) {
            throw reader.invalid(issues);
        }
        return value;
    }

    private JsonValue present(String field) {
        JsonValue value = json.get(field);
        if (value == null) {
            add(field, "REQUIRED", FIELD + name(field) + " is required.");
        } else if (isNull(value)) {
            add(field, "MUST_NOT_BE_NULL", FIELD + name(field) + " must not be null.");
        }
        return value;
    }

    private void invalidType(JsonValue value, String field, String expected) {
        if (value != null && !isNull(value)) {
            add(field, INVALID_TYPE, FIELD + name(field) + " must be " + expected + ".");
        }
    }

    private boolean isNull(JsonValue value) {
        return value.getValueType() == JsonValue.ValueType.NULL;
    }

    private void add(String field, String title, String description) {
        issues.add(reader.issue(name(field), title, description));
    }

    private String name(String field) {
        return prefix + field;
    }
}
