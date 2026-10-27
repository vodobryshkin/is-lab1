package com.github.vodobryshkin.islab1.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO с ID созданного объекта.
 */
@Data
@AllArgsConstructor
public class IdResponse {
    private long id;
}
