package com.github.vodobryshkin.islab1.backend.dto;

/**
 * Проверенные параметры страницы списка.
 *
 * @param page номер страницы с 1
 * @param pageSize размер страницы от 1 до 100
 * @param sortBy поле сортировки из разрешённого набора ресурса
 * @param ascending {@code true} для порядка по возрастанию
 */
public record ListQuery(int page, int pageSize, String sortBy, boolean ascending) {
}
