package com.github.vodobryshkin.islab1.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Проверенные параметры страницы списка: номер, размер, поле и направление сортировки.
 */
@Data
@AllArgsConstructor
public class ListQuery {
    private int page;

    private int pageSize;

    private String sortBy;

    private boolean ascending;
}
