package com.github.vodobryshkin.islab1.backend.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

/**
 * Тестовый класс для преобразования ZonedDateTime в значение колонки TIMESTAMPTZ.
 */
class ZonedDateTimeConverterTest {
    private final ZonedDateTimeConverter sut = new ZonedDateTimeConverter();

    @Test
    void keeps_instant_and_offset_on_write_to_column() {
        // Arrange
        ZonedDateTime attribute = ZonedDateTime.parse("2026-01-02T03:04:05+03:00[Europe/Moscow]");

        // Act
        OffsetDateTime actual = sut.convertToDatabaseColumn(attribute);

        // Assert
        assertThat(actual).isEqualTo(OffsetDateTime.parse("2026-01-02T03:04:05+03:00"));
    }

    @Test
    void keeps_instant_and_offset_on_read_from_column() {
        // Arrange
        OffsetDateTime dbData = OffsetDateTime.parse("2026-01-02T00:04:05Z");

        // Act
        ZonedDateTime actual = sut.convertToEntityAttribute(dbData);

        // Assert
        assertThat(actual).isEqualTo(ZonedDateTime.parse("2026-01-02T00:04:05Z"));
    }

    @Test
    void converts_null_attribute_to_null_column() {
        // Act
        OffsetDateTime actual = sut.convertToDatabaseColumn(null);

        // Assert
        assertThat(actual).isNull();
    }

    @Test
    void converts_null_column_to_null_attribute() {
        // Act
        ZonedDateTime actual = sut.convertToEntityAttribute(null);

        // Assert
        assertThat(actual).isNull();
    }
}
