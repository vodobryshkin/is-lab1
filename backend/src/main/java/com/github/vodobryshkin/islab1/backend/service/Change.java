package com.github.vodobryshkin.islab1.backend.service;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Результат составного изменения со ссылками на другие объекты.
 * При отказе subject и subjectId называют объект, из-за которого изменение отклонено.
 */
@Data
@AllArgsConstructor
public class Change {
    private Outcome outcome;

    private Long id;

    private String subject;

    private Long subjectId;
}
