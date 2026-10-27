package com.github.vodobryshkin.islab1.backend.resource;

import jakarta.json.JsonNumber;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.bind.JsonbException;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * Читает значение JSON только нужного типа, без округления и неявных преобразований.
 * Без него JSON-B превратил бы число в строку, а 1.5 -- в целое 1.
 */
public class StrictDeserializer<T> implements JsonbDeserializer<T> {
    private final JsonValue.ValueType expected;
    private final Function<JsonValue, T> converter;

    /**
     * Создаёт десериализатор.
     *
     * @param expected допустимый тип значения JSON
     * @param converter преобразование значения
     */
    public StrictDeserializer(JsonValue.ValueType expected, Function<JsonValue, T> converter) {
        this.expected = expected;
        this.converter = converter;
    }

    @Override
    public T deserialize(JsonParser parser, DeserializationContext context, Type type) {
        JsonValue value = parser.getValue();

        if (value.getValueType() == JsonValue.ValueType.NULL) {
            return null;
        }

        if (value.getValueType() != expected) {
            throw new JsonbException("Unexpected JSON value type.");
        }

        return converter.apply(value);
    }

    /**
     * Читает строку.
     */
    public static class Text extends StrictDeserializer<String> {
        /**
         * Создаёт десериализатор строк.
         */
        public Text() {
            super(JsonValue.ValueType.STRING, value -> ((JsonString) value).getString());
        }
    }

    /**
     * Читает целое число int32.
     */
    public static class Int32 extends StrictDeserializer<Integer> {
        /**
         * Создаёт десериализатор int32.
         */
        public Int32() {
            super(JsonValue.ValueType.NUMBER,
                    value -> ((JsonNumber) value).bigDecimalValue().intValueExact());
        }
    }

    /**
     * Читает целое число int64.
     */
    public static class Int64 extends StrictDeserializer<Long> {
        /**
         * Создаёт десериализатор int64.
         */
        public Int64() {
            super(JsonValue.ValueType.NUMBER,
                    value -> ((JsonNumber) value).bigDecimalValue().longValueExact());
        }
    }

    /**
     * Читает число float32.
     */
    public static class Float32 extends StrictDeserializer<Float> {
        /**
         * Создаёт десериализатор float32.
         */
        public Float32() {
            super(JsonValue.ValueType.NUMBER,
                    value -> ((JsonNumber) value).bigDecimalValue().floatValue());
        }
    }

    /**
     * Читает число float64.
     */
    public static class Float64 extends StrictDeserializer<Double> {
        /**
         * Создаёт десериализатор float64.
         */
        public Float64() {
            super(JsonValue.ValueType.NUMBER, value -> ((JsonNumber) value).doubleValue());
        }
    }
}
