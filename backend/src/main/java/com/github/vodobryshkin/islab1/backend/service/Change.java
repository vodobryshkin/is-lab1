package com.github.vodobryshkin.islab1.backend.service;

/**
 * Результат составного изменения, в котором участвуют ссылки на другие объекты.
 *
 * @param outcome итог
 * @param id ID созданного объекта при успешном создании, иначе {@code null}
 * @param missingType вид ненайденного объекта, например {@code "address"},
 *     при {@link Outcome#REFERENCE_NOT_FOUND}, иначе {@code null}
 * @param missingId ID ненайденного объекта или {@code null}
 */
public record Change(Outcome outcome, Long id, String missingType, Long missingId) {
}
