package com.github.vodobryshkin.islab1.backend.resource;

import com.github.vodobryshkin.islab1.backend.dto.ValidationIssue;
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
 * Поля одного JSON-объекта тела запроса с накопленными нарушениями.
 * Нарушения вложенных объектов получают имя с префиксом, например location.x.
 */
@SuppressWarnings("PMD.GodClass")
public final class RequestFields {
    private final RequestReader reader;
    private final JsonObject json;
    private final String prefix;
    private final List<ValidationIssue> issues;

    /** Префиксы вложенных ссылок {id}: их поля нового объекта не проверяются. */
    private final Set<String> references;

    RequestFields(RequestReader reader, JsonObject json) {
        this(reader, json, "", new ArrayList<>(), new HashSet<>());
    }

    private RequestFields(RequestReader reader, JsonObject json, String prefix,
            List<ValidationIssue> issues, Set<String> references) {
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
        for (String key : json.keySet()) {
            if (!names.contains(key)) {
                reject(key, "UNKNOWN_FIELD", name(key) + " is not supported.");
            }
        }
        return this;
    }

    /**
     * Читает вложенный объект.
     *
     * @param field имя поля
     * @param nullable допускается ли null
     * @return поля вложенного объекта или null, если значение null или неверно
     */
    public RequestFields object(String field, boolean nullable) {
        JsonValue value = json.get(field);
        if (value instanceof JsonObject object) {
            return new RequestFields(reader, object, name(field) + ".", issues, references);
        }
        if (value == null || !nullable && isNull(value)) {
            present(field);
        } else if (!isNull(value)) {
            reject(field, "INVALID_TYPE", name(field) + " must be an object.");
        }
        return null;
    }

    /**
     * Различает варианты вложенного объекта «ссылка {id}» и «новый объект».
     * У ссылки поля нового объекта запрещены ("MUTUALLY_EXCLUSIVE") и не проверяются.
     *
     * @param newObjectFields поля варианта «новый объект»
     * @return true, если это ссылка
     */
    boolean isReference(String... newObjectFields) {
        List<String> allowed = new ArrayList<>(List.of(newObjectFields));
        allowed.add("id");
        allowOnly(allowed.toArray(String[]::new));
        if (!json.containsKey("id")) {
            return false;
        }
        if (List.of(newObjectFields).stream().anyMatch(json::containsKey)) {
            reject("id", "MUTUALLY_EXCLUSIVE", "Use either id or the fields of a new object.");
        }
        references.add(prefix);
        return true;
    }

    /**
     * Читает положительный ID существующего объекта.
     *
     * @param field имя поля
     * @return ID или null, если поле неверно
     */
    public Long id(String field) {
        Long value = int64(field);
        if (value != null && value <= 0) {
            reject(field, "MUST_BE_POSITIVE", name(field) + " must be greater than zero.");
            return null;
        }
        return value;
    }

    /**
     * Читает строку.
     *
     * @param field имя поля
     * @return значение или null, если поле неверно
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
     * @param nullable допускается ли null
     * @param <E> тип перечисления
     * @return значение или null
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
            reject(field, "INVALID_ENUM_VALUE", name(field) + " has an unsupported value.");
            return null;
        }
    }

    /**
     * Читает дату и время RFC 3339 со смещением, например 2026-10-02T17:16:48Z.
     *
     * @param field имя поля
     * @return значение или null, если поле неверно
     */
    public ZonedDateTime dateTime(String field) {
        String text = string(field);
        if (text == null) {
            return null;
        }
        try {
            return OffsetDateTime.parse(text).toZonedDateTime();
        } catch (DateTimeParseException exception) {
            reject(field, "INVALID_DATE_TIME", name(field) + " must be an RFC 3339 date-time.");
            return null;
        }
    }

    /**
     * Читает число типа float64.
     *
     * @param field имя поля
     * @return значение или null, если поле неверно
     */
    public Double float64(String field) {
        BigDecimal value = decimal(field, Double.MAX_VALUE, "float64");
        return value == null ? null : value.doubleValue();
    }

    /**
     * Читает число типа float32.
     *
     * @param field имя поля
     * @return значение или null, если поле неверно
     */
    public Float float32(String field) {
        BigDecimal value = decimal(field, Float.MAX_VALUE, "float32");
        return value == null ? null : value.floatValue();
    }

    /**
     * Читает целое число типа int32.
     *
     * @param field имя поля
     * @return значение или null, если поле неверно
     */
    public Integer int32(String field) {
        Long value = int64(field);
        if (value != null && (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE)) {
            reject(field, "OUT_OF_RANGE", name(field) + " must fit into int32.");
            return null;
        }
        return value == null ? null : value.intValue();
    }

    /**
     * Читает целое число типа int64.
     *
     * @param field имя поля
     * @return значение или null, если поле неверно
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
            reject(field, "OUT_OF_RANGE", name(field) + " must fit into int64.");
            return null;
        }
    }

    /**
     * Проверяет собранный объект через Jakarta Validation. Поля с ошибкой структуры
     * представлены только ею; поля ссылок {id} не проверяются как новый объект.
     *
     * @param value объект из прочитанных полей; null, если поле с ошибкой было
     *     единственным значением и проверять нечего, кроме ошибок самих полей
     * @param serverFields поля, которые заполняет сервер, а не клиент; их нарушения
     *     не относятся к запросу
     * @param <T> тип объекта
     * @return тот же объект
     */
    public <T> T validate(T value, String... serverFields) {
        Set<String> invalidFields = new HashSet<>(List.of(serverFields));
        issues.forEach(issue -> invalidFields.add(issue.getField()));
        if (value != null) {
            reader.violations(value).stream()
                    .filter(issue -> !invalidFields.contains(issue.getField()))
                    .filter(issue -> references.stream().noneMatch(issue.getField()::startsWith))
                    .forEach(issues::add);
        }
        if (!issues.isEmpty()) {
            throw reader.invalid(issues);
        }
        return value;
    }

    private BigDecimal decimal(String field, double max, String type) {
        JsonValue value = present(field);
        if (!(value instanceof JsonNumber number)) {
            invalidType(value, field, "a number");
            return null;
        }
        if (number.bigDecimalValue().abs().compareTo(BigDecimal.valueOf(max)) > 0) {
            reject(field, "OUT_OF_RANGE", name(field) + " must fit into " + type + ".");
            return null;
        }
        return number.bigDecimalValue();
    }

    /**
     * Есть ли ключ в объекте, даже со значением null.
     *
     * @param field имя поля
     * @return true, если ключ присутствует
     */
    boolean has(String field) {
        return json.containsKey(field);
    }

    /**
     * Равно ли значение поля null.
     *
     * @param field имя поля
     * @return true, если ключ присутствует со значением null
     */
    boolean hasNull(String field) {
        return json.containsKey(field) && isNull(json.get(field));
    }

    /**
     * Отмечает вложенный объект как ссылку по ID: его поля нового объекта не проверяются.
     *
     * @param field имя поля вложенного объекта
     */
    void markReference(String field) {
        references.add(name(field) + ".");
    }

    private JsonValue present(String field) {
        JsonValue value = json.get(field);
        if (value == null) {
            reject(field, "REQUIRED", name(field) + " is required.");
        } else if (isNull(value)) {
            reject(field, "MUST_NOT_BE_NULL", name(field) + " must not be null.");
        }
        return value;
    }

    private void invalidType(JsonValue value, String field, String expected) {
        if (value != null && !isNull(value)) {
            reject(field, "INVALID_TYPE", name(field) + " must be " + expected + ".");
        }
    }

    private boolean isNull(JsonValue value) {
        return value.getValueType() == JsonValue.ValueType.NULL;
    }

    /**
     * Добавляет нарушение поля.
     *
     * @param field имя поля
     * @param code код нарушения
     * @param message описание
     */
    void reject(String field, String code, String message) {
        issues.add(new ValidationIssue(name(field), code, message));
    }

    private String name(String field) {
        return prefix + field;
    }
}
