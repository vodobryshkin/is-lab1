package com.github.vodobryshkin.islab1.backend.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * Преобразует время с часовым поясом для сохранения в PostgreSQL.
 */
@Converter(autoApply = true)
public class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime, OffsetDateTime> {
    @Override
    public OffsetDateTime convertToDatabaseColumn(ZonedDateTime attribute) {
        return attribute == null ? null : attribute.toOffsetDateTime();
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(OffsetDateTime dbData) {
        return dbData == null ? null : dbData.toZonedDateTime();
    }
}
