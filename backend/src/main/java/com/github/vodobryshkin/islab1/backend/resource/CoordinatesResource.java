package com.github.vodobryshkin.islab1.backend.resource;

import com.github.vodobryshkin.islab1.backend.domain.Coordinates;
import com.github.vodobryshkin.islab1.backend.dto.CoordinatesResponse;
import com.github.vodobryshkin.islab1.backend.dto.ErrorResponse;
import com.github.vodobryshkin.islab1.backend.dto.IdResponse;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import com.github.vodobryshkin.islab1.backend.dto.PageResponse;
import com.github.vodobryshkin.islab1.backend.service.CoordinatesService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.util.Optional;
import java.util.Set;

/**
 * Операции /coordinates. Вход и CSRF проверяются до вызова методов.
 */
@Path("/coordinates")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class CoordinatesResource {
    private static final Set<String> SORT_FIELDS = Set.of("id", "x", "y");
    private static final String NOT_FOUND = "Coordinates with id=%d do not exist.";

    private final CoordinatesService service;
    private final RequestReader requestReader;

    /**
     * Создаёт ресурс.
     *
     * @param service операции с координатами
     * @param requestReader чтение запросов
     */
    @Inject
    public CoordinatesResource(CoordinatesService service, RequestReader requestReader) {
        this.service = service;
        this.requestReader = requestReader;
    }

    /**
     * Нужен CDI для прокси и Jakarta REST, который требует публичный конструктор.
     */
    public CoordinatesResource() {
        this(null, null);
    }

    /**
     * Создаёт координаты.
     *
     * @param body тело запроса
     * @param uriInfo адрес запроса для заголовка Location
     * @return 201 с ID
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String body, @Context UriInfo uriInfo) {
        Long id = service.create(readCoordinates(body));
        return Response.created(uriInfo.getAbsolutePathBuilder().path(id.toString()).build())
                .entity(new IdResponse(id))
                .build();
    }

    /**
     * Возвращает координаты.
     *
     * @param rawId ID из пути
     * @return 200 с координатами или 404
     */
    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String rawId) {
        long id = requestReader.id(rawId);
        Optional<Coordinates> coordinates = service.find(id);
        if (coordinates.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(NOT_FOUND.formatted(id)))
                    .build();
        }
        return Response.ok().entity(new CoordinatesResponse(coordinates.get())).build();
    }

    /**
     * Возвращает страницу координат.
     *
     * @param uriInfo параметры страницы и сортировки
     * @return 200 со страницей
     */
    @GET
    public Response list(@Context UriInfo uriInfo) {
        ListQuery query = requestReader.listQuery(uriInfo.getQueryParameters(), SORT_FIELDS);
        PageResponse<CoordinatesResponse> page = new PageResponse<>(
                service.findPage(query).stream().map(CoordinatesResponse::new).toList(),
                query.getPage(), query.getPageSize(), service.count());
        return Response.ok().entity(page).build();
    }

    /**
     * Заменяет все поля координат.
     *
     * @param rawId ID из пути
     * @param body тело запроса
     * @return 204 или 404
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") String rawId, String body) {
        long id = requestReader.id(rawId);
        if (!service.update(id, readCoordinates(body))) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(NOT_FOUND.formatted(id)))
                    .build();
        }
        return Response.noContent().build();
    }

    /**
     * Удаляет координаты, если на них не ссылаются работники.
     *
     * @param rawId ID из пути
     * @return 204, 404 или 409
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String rawId) {
        long id = requestReader.id(rawId);
        return switch (service.delete(id)) {
            case DONE -> Response.noContent().build();
            case IN_USE -> Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse(
                            "The coordinates cannot be deleted while workers reference them."))
                    .build();
            default -> Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(NOT_FOUND.formatted(id)))
                    .build();
        };
    }

    private Coordinates readCoordinates(String body) {
        RequestFields fields = requestReader.read(body).allowOnly("x", "y");
        return fields.validate(new Coordinates(null, fields.int32("x"), fields.int64("y")));
    }
}
