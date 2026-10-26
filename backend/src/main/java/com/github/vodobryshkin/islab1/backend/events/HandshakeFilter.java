package com.github.vodobryshkin.islab1.backend.events;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Проверяет Upgrade на {@code /api/v1/events} до открытия соединения: {@code Origin}
 * из списка {@code IS_LAB1_ALLOWED_ORIGINS} (через запятую; по умолчанию сервер
 * разработки фронтенда), иначе 403; параметры URL не поддерживаются, иначе 400.
 * Вход проверяет раньше контейнер по ограничению {@code web.xml}.
 */
@WebFilter("/api/v1/events")
public class HandshakeFilter extends HttpFilter {
    private static final long serialVersionUID = 1L;

    private final Set<String> allowedOrigins = Arrays.stream(
                    System.getenv().getOrDefault("IS_LAB1_ALLOWED_ORIGINS",
                            "http://localhost:5173").split(","))
            .map(String::strip)
            .filter(origin -> !origin.isEmpty())
            .collect(Collectors.toUnmodifiableSet());

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        String origin = request.getHeader("Origin");
        if (origin == null || !allowedOrigins.contains(origin)) {
            reject(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden",
                    "The Origin header is missing or not allowed.");
            return;
        }
        if (request.getQueryString() != null) {
            reject(response, HttpServletResponse.SC_BAD_REQUEST, "Bad Request",
                    "URL parameters are not supported.");
            return;
        }
        chain.doFilter(request, response);
    }

    /** Контракт не задаёт тип проблемы, поэтому {@code about:blank} из RFC 9457. */
    private void reject(HttpServletResponse response, int status, String title, String detail)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"type\":\"about:blank\",\"title\":\"" + title
                + "\",\"status\":" + status + ",\"detail\":\"" + detail + "\"}");
    }
}
