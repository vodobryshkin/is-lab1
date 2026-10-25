package com.github.vodobryshkin.islab1.backend.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * Преобразует {@link ZonedDateTime} в {@link OffsetDateTime} для колонок {@code TIMESTAMPTZ}.
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
