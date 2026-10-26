package com.github.vodobryshkin.islab1.backend.resource;

import com.github.vodobryshkin.islab1.backend.domain.User;
import com.github.vodobryshkin.islab1.backend.dto.CredentialsRequest;
import com.github.vodobryshkin.islab1.backend.dto.ProblemDetails;
import com.github.vodobryshkin.islab1.backend.security.CsrfProtection;
import com.github.vodobryshkin.islab1.backend.service.UserService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.credential.Password;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.Map;

/**
 * Операции {@code /auth}: CSRF-токен, регистрация, вход, текущий пользователь и выход.
 * CSRF изменяющих запросов проверяет {@link CsrfProtection} до вызова методов ресурса.
 */
@Path("/auth")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {
    /** SQLSTATE нарушения уникальности. */
    private static final String UNIQUE_VIOLATION = "23505";

    private final UserService userService;
    private final CsrfProtection csrfProtection;
    private final SecurityContext securityContext;
    private final CredentialsReader credentialsReader;

    /**
     * Создаёт ресурс.
     *
     * @param userService пользователи
     * @param csrfProtection CSRF-токены сессии
     * @param securityContext контекст Jakarta Security
     * @param credentialsReader чтение логина и пароля из тела запроса
     */
    @Inject
    public AuthResource(UserService userService, CsrfProtection csrfProtection,
            SecurityContext securityContext, CredentialsReader credentialsReader) {
        this.userService = userService;
        this.csrfProtection = csrfProtection;
        this.securityContext = securityContext;
        this.credentialsReader = credentialsReader;
    }

    /** Нужен CDI для прокси и Jakarta REST, который требует публичный конструктор. */
    public AuthResource() {
        this(null, null, null, null);
    }

    /**
     * Возвращает CSRF-токен сессии, создавая сессию при необходимости.
     *
     * @param request текущий запрос
     * @return 200 с токеном
     */
    @GET
    @Path("/csrf")
    public Response getCsrfToken(@Context HttpServletRequest request) {
        String token = csrfProtection.tokenOf(request.getSession(true));
        return Response.ok(Map.of("csrf_token", token)).cacheControl(noStore()).build();
    }

    /**
     * Регистрирует пользователя без входа.
     *
     * @param body тело запроса
     * @return 201 с ID, 409 при занятом логине
     */
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(String body) {
        CredentialsRequest credentials = credentialsReader.read(body);
        try {
            Long id = userService.register(credentials.getUsername(), credentials.getPassword());
            return Response.status(Response.Status.CREATED).entity(Map.of("id", id)).build();
        } catch (PersistenceException exception) {
            if (!isUniqueViolation(exception)) {
                throw exception;
            }
            return new ProblemDetails(409, "/problems/username-already-exists",
                    "Username already exists", "A user with this username already exists.")
                    .toResponse();
        }
    }

    /**
     * Проверяет логин и пароль через Jakarta Security и связывает вход с сессией.
     * После успеха меняет ID сессии, чтобы прежний ID не давал доступа, и CSRF-токен.
     *
     * <p>Прежний вход сессии завершается до проверки: {@code @AutoApplySession} в Soteria
     * восстанавливает сохранённого пользователя без вызова механизма даже при
     * {@code newAuthentication(true)}, и тогда любой пароль считался бы верным.
     *
     * @param body тело запроса
     * @param request текущий запрос
     * @param response текущий ответ
     * @return 200 с пользователем или 400 при неверных данных
     * @throws ServletException если контейнер не смог завершить прежний вход
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(String body, @Context HttpServletRequest request,
            @Context HttpServletResponse response) throws ServletException {
        CredentialsRequest credentials = credentialsReader.read(body);
        if (request.getUserPrincipal() != null) {
            request.logout();
        }
        AuthenticationStatus status = securityContext.authenticate(request, response,
                AuthenticationParameters.withParams()
                        .newAuthentication(true)
                        .credential(new UsernamePasswordCredential(credentials.getUsername(),
                                new Password(credentials.getPassword()))));
        if (status != AuthenticationStatus.SUCCESS) {
            return new ProblemDetails(400, "/problems/invalid-credentials", "Invalid credentials",
                    "Username or password is incorrect.").toResponse();
        }
        request.changeSessionId();
        csrfProtection.replace(request.getSession());
        return currentUser(credentials.getUsername());
    }

    /**
     * Возвращает пользователя текущей сессии. Без входа запрос сюда не доходит:
     * адрес защищён в {@code web.xml}, и механизм аутентификации отвечает 401 раньше.
     *
     * @param request текущий запрос
     * @return 200 с пользователем
     */
    @GET
    @Path("/session")
    public Response getSession(@Context HttpServletRequest request) {
        return currentUser(request.getUserPrincipal().getName());
    }

    /**
     * Завершает вход и инвалидирует сессию вместе с CSRF-токеном. Cookie с
     * {@code Max-Age=0} при инвалидации отправляет сам контейнер.
     *
     * @param request текущий запрос
     * @return 204
     * @throws ServletException если контейнер не смог завершить вход
     */
    @POST
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request) throws ServletException {
        request.logout();
        request.getSession().invalidate();
        return Response.noContent().cacheControl(noStore()).build();
    }

    private Response currentUser(String username) {
        User user = userService.findByUsername(username).orElseThrow();
        Map<String, Object> body = Map.of("id", user.getId(), "username", user.getUsername());
        return Response.ok(body).cacheControl(noStore()).build();
    }

    private boolean isUniqueViolation(Throwable exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof SQLException sqlException
                    && UNIQUE_VIOLATION.equals(sqlException.getSQLState())) {
                return true;
            }
        }
        return false;
    }

    private CacheControl noStore() {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoStore(true);
        return cacheControl;
    }
}
