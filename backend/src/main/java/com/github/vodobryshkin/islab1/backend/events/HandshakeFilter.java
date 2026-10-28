package com.github.vodobryshkin.islab1.backend.events;

import com.github.vodobryshkin.islab1.backend.dto.common.ErrorResponse;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
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
 * Проверяет Origin и отсутствие параметров URL у запроса Upgrade на /api/v1/events.
 */
@WebFilter("/api/v1/events")
public class HandshakeFilter extends HttpFilter {
    private static final long serialVersionUID = 1L;
    private static final Jsonb JSONB = JsonbBuilder.create();

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
            reject(response, HttpServletResponse.SC_FORBIDDEN,
                    "The Origin header is missing or not allowed.");
            return;
        }

        if (request.getQueryString() != null) {
            reject(response, HttpServletResponse.SC_BAD_REQUEST,
                    "URL parameters are not supported.");
            return;
        }

        chain.doFilter(request, response);
    }

    private void reject(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONB.toJson(new ErrorResponse(message), response.getWriter());
    }
}
