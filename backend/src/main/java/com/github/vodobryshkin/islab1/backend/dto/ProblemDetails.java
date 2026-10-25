package com.github.vodobryshkin.islab1.backend.dto;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Описание ошибки в формате RFC 9457. Пустые {@code detail} и {@code errors} не выводятся.
 */
@Data
@AllArgsConstructor
public class ProblemDetails {
    private String type;

    private String title;

    private int status;

    private String detail;

    /** Нарушения по полям для {@code /problems/validation-failed}. */
    private List<Issue> errors;

    /**
     * Создаёт описание ошибки без нарушений по полям.
     *
     * @param status HTTP-статус
     * @param type URI-reference типа проблемы
     * @param title краткое название
     * @param detail подробности для клиента
     */
    public ProblemDetails(int status, String type, String title, String detail) {
        this(type, title, status, detail, null);
    }

    /**
     * Создаёт ответ с этим описанием и тем же HTTP-статусом.
     *
     * @return ответ Jakarta REST
     */
    public Response toResponse() {
        return Response.status(status).type(MediaType.APPLICATION_JSON_TYPE).entity(this).build();
    }

    /**
     * Нарушение правила в одном поле запроса.
     */
    @Data
    @AllArgsConstructor
    public static class Issue {
        private String field;

        /** Машинный код из контракта, например {@code OUT_OF_RANGE}. */
        private String title;

        private String description;
    }
}
