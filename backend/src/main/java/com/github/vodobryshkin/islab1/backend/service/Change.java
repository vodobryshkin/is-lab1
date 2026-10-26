package com.github.vodobryshkin.islab1.backend.service;

/**
 * Результат составного изменения, в котором участвуют ссылки на другие объекты.
 *
 * @param outcome итог
 * @param id ID созданного объекта при успешном создании, иначе {@code null}
 * @param subject вид объекта, из-за которого изменение отклонено, например
 *     {@code "address"} при {@link Outcome#REFERENCE_NOT_FOUND} или {@code "person"}
 *     при {@link Outcome#CONFLICT}; иначе {@code null}
 * @param subjectId ID этого объекта или {@code null}
 */
public record Change(Outcome outcome, Long id, String subject, Long subjectId) {
}
