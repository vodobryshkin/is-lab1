package com.github.vodobryshkin.islab1.backend.resource;

import com.github.vodobryshkin.islab1.backend.dto.CredentialsRequest;
import com.github.vodobryshkin.islab1.backend.dto.CsrfTokenResponse;
import com.github.vodobryshkin.islab1.backend.dto.CurrentUserResponse;
import com.github.vodobryshkin.islab1.backend.dto.ErrorResponse;
import com.github.vodobryshkin.islab1.backend.dto.IdResponse;
import com.github.vodobryshkin.islab1.backend.events.EventSessions;
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

/**
 * Операции /auth: CSRF-токен, регистрация, вход, текущий пользователь и выход.
 * CSRF изменяющих запросов проверяет CsrfProtection до вызова методов ресурса.
 */
@Path("/auth")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {
    private static final String UNIQUE_VIOLATION = "23505";

    private final UserService userService;
    private final CsrfProtection csrfProtection;
    private final SecurityContext securityContext;
    private final RequestReader requestReader;
    private final EventSessions eventSessions;

    /**
     * Создаёт ресурс.
     *
     * @param userService пользователи
     * @param csrfProtection CSRF-токены сессии
     * @param securityContext контекст Jakarta Security
     * @param requestReader чтение тела запроса
     * @param eventSessions WebSocket-соединения, завершаемые вместе со входом
     */
    @Inject
    public AuthResource(UserService userService, CsrfProtection csrfProtection,
            SecurityContext securityContext, RequestReader requestReader,
            EventSessions eventSessions) {
        this.userService = userService;
        this.csrfProtection = csrfProtection;
        this.securityContext = securityContext;
        this.requestReader = requestReader;
        this.eventSessions = eventSessions;
    }

    /**
     * Нужен CDI для прокси и Jakarta REST, который требует публичный конструктор.
     */
    public AuthResource() {
        this(null, null, null, null, null);
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

        return Response.ok().entity(new CsrfTokenResponse(token)).cacheControl(noStore()).build();
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
        CredentialsRequest credentials = readCredentials(body);

        try {
            Long id = userService.register(credentials.getUsername(), credentials.getPassword());
            return Response.status(Response.Status.CREATED).entity(new IdResponse(id)).build();
        } catch (PersistenceException exception) {
            if (!isUniqueViolation(exception)) {
                throw exception;
            }
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse("A user with this username already exists."))
                    .build();
        }
    }

    /**
     * Выполняет вход, меняет ID сессии и CSRF-токен.
     * Прежний вход завершается до проверки: иначе AutoApplySession принял бы любой пароль.
     *
     * @param body тело запроса
     * @param request текущий запрос
     * @param response текущий ответ
     * @return 200 с пользователем или 400 при неверных данных
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(String body, @Context HttpServletRequest request,
            @Context HttpServletResponse response) throws ServletException {
        CredentialsRequest credentials = readCredentials(body);

        if (request.getUserPrincipal() != null) {
            eventSessions.end(request.getSession());
            request.logout();
        }

        AuthenticationStatus status = securityContext.authenticate(request, response,
                AuthenticationParameters.withParams()
                        .newAuthentication(true)
                        .credential(new UsernamePasswordCredential(credentials.getUsername(),
                                new Password(credentials.getPassword()))));

        if (status != AuthenticationStatus.SUCCESS) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Username or password is incorrect."))
                    .build();
        }

        request.changeSessionId();
        csrfProtection.replace(request.getSession());

        return currentUser(credentials.getUsername());
    }

    /**
     * Возвращает пользователя текущей сессии. Без входа запрос сюда не доходит:
     * адрес защищён в web.xml, и механизм аутентификации отвечает 401 раньше.
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
     * Завершает вход и инвалидирует сессию вместе с CSRF-токеном.
     * WebSocket-соединения закрываются заранее, чтобы клиент получил SESSION_ENDED.
     *
     * @param request текущий запрос
     * @return 204
     */
    @POST
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request) throws ServletException {
        eventSessions.end(request.getSession());
        request.logout();
        request.getSession().invalidate();

        return Response.noContent().cacheControl(noStore()).build();
    }

    private Response currentUser(String username) {
        CurrentUserResponse body = userService.findByUsername(username)
                .map(user -> new CurrentUserResponse(user.getId(), user.getUsername()))
                .orElseThrow();

        return Response.ok().entity(body).cacheControl(noStore()).build();
    }

    private CredentialsRequest readCredentials(String body) {
        RequestFields fields = requestReader.read(body);

        return fields.validate(
                new CredentialsRequest(fields.string("username"), fields.string("password")));
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
