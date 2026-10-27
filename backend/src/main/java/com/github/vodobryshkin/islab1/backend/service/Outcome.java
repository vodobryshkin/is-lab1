package com.github.vodobryshkin.islab1.backend.service;

/**
 * Результат изменения или удаления объекта.
 */
public enum Outcome {
    DONE,
    NOT_FOUND,
    REFERENCE_NOT_FOUND,
    IN_USE,
    CONFLICT
}
