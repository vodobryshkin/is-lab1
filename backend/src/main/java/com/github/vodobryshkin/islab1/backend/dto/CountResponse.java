package com.github.vodobryshkin.islab1.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO с количеством объектов.
 */
@Data
@AllArgsConstructor
public class CountResponse {
    private long count;
}
