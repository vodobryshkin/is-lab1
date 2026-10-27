package com.github.vodobryshkin.islab1.backend.security;

import com.github.vodobryshkin.islab1.backend.dto.ErrorResponse;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Set;

/**
 * Выдаёт CSRF-токены сессии и проверяет их у всех изменяющих запросов.
 * Проверка выполняется до метода ресурса, в том числе до входа.
 */
@Provider
@ApplicationScoped
@Priority(Priorities.AUTHORIZATION)
public class CsrfProtection implements ContainerRequestFilter {
    private static final String HEADER = "X-CSRF-Token";
    private static final String SESSION_ATTRIBUTE = CsrfProtection.class.getName();
    private static final Set<String> SAFE_METHODS = Set.of("GET", "HEAD", "OPTIONS");
    private static final int TOKEN_BYTES = 32;

    private final SecureRandom random = new SecureRandom();

    @Context
    private HttpServletRequest request;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (SAFE_METHODS.contains(requestContext.getMethod())) {
            return;
        }

        HttpSession session = request.getSession(false);
        Object expected = session == null ? null : session.getAttribute(SESSION_ATTRIBUTE);
        String actual = requestContext.getHeaderString(HEADER);
        boolean valid = expected instanceof String token && actual != null
                && MessageDigest.isEqual(token.getBytes(StandardCharsets.UTF_8),
                        actual.getBytes(StandardCharsets.UTF_8));

        if (!valid) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new ErrorResponse("The X-CSRF-Token header is missing or does not "
                            + "match the current session."))
                    .build());
        }
    }

    /**
     * Возвращает токен сессии, создавая его при первом обращении.
     *
     * @param session текущая сессия
     * @return токен
     */
    public String tokenOf(HttpSession session) {
        Object token = session.getAttribute(SESSION_ATTRIBUTE);

        return token instanceof String existing ? existing : replace(session);
    }

    /**
     * Заменяет токен сессии новым, прежний перестаёт действовать.
     *
     * @param session текущая сессия
     * @return новый токен
     */
    public String replace(HttpSession session) {
        byte[] bytes = new byte[TOKEN_BYTES];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(SESSION_ATTRIBUTE, token);

        return token;
    }
}
