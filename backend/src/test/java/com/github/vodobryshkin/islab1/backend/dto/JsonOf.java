package com.github.vodobryshkin.islab1.backend.dto;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.io.StringReader;

/**
 * Сериализует DTO так же, как сервер (JSON-B без дополнительной конфигурации),
 * и разбирает результат для сравнения без учёта порядка свойств.
 */
final class JsonOf implements AutoCloseable {
    private final Jsonb jsonb = JsonbBuilder.create();

    JsonObject serialize(Object dto) {
        return parse(jsonb.toJson(dto));
    }

    JsonObject parse(String json) {
        try (JsonReader reader = Json.createReader(new StringReader(json))) {
            return reader.readObject();
        }
    }

    @Override
    public void close() {
        try {
            jsonb.close();
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}
