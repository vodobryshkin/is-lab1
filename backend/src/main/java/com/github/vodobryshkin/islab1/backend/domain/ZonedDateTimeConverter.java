package com.github.vodobryshkin.islab1.backend.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * Преобразует {@link ZonedDateTime} в {@link OffsetDateTime} для колонок {@code TIMESTAMPTZ}.
 *
 * <p>JPA 3.1 не включает {@link ZonedDateTime} в список поддерживаемых временных типов,
 * и EclipseLink без преобразования сохраняет его как сериализованный {@code bytea}.
 * {@code TIMESTAMPTZ} хранит момент времени без идентификатора часового пояса,
 * поэтому после чтения значение содержит только смещение.
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
