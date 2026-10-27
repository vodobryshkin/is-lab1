package com.github.vodobryshkin.islab1.backend.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO со страницей объектов списка.
 */
@Data
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> items;

    private int page;

    @JsonbProperty("page_size")
    private int pageSize;

    @JsonbProperty("total_items")
    private long totalItems;
}
