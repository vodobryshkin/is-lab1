package com.github.vodobryshkin.islab1.backend.resource;

import com.github.vodobryshkin.islab1.backend.domain.Coordinates;
import com.github.vodobryshkin.islab1.backend.dto.CoordinatesResponse;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import com.github.vodobryshkin.islab1.backend.dto.ProblemDetails;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Операции {@code /coordinates}. Вход и CSRF проверяются до вызова методов.
 */
@Path("/coordinates")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class CoordinatesResource {
    private static final Set<String> SORT_FIELDS = Set.of("id", "x", "y");

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

    /** Нужен CDI для прокси и Jakarta REST, который требует публичный конструктор. */
    public CoordinatesResource() {
        this(null, null);
    }

    /**
     * Создаёт координаты.
     *
     * @param body тело запроса
     * @param uriInfo адрес запроса для заголовка {@code Location}
     * @return 201 с ID
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String body, @Context UriInfo uriInfo) {
        Long id = service.create(readCoordinates(body));
        return Response.created(uriInfo.getAbsolutePathBuilder().path(id.toString()).build())
                .entity(Map.of("id", id))
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
        return service.find(id)
                .map(coordinates -> Response.ok(toResponse(coordinates)).build())
                .orElseGet(() -> notFound(id));
    }

    /**
     * Возвращает страницу координат.
     *
     * @param uriInfo параметры {@code page}, {@code page_size}, {@code sort_by}, {@code sort_order}
     * @return 200 со страницей
     */
    @GET
    public Response list(@Context UriInfo uriInfo) {
        ListQuery query = requestReader.listQuery(uriInfo.getQueryParameters(), SORT_FIELDS);
        List<CoordinatesResponse> items =
                service.findPage(query).stream().map(this::toResponse).toList();
        return Response.ok(Map.of("items", items, "page", query.page(),
                "page_size", query.pageSize(), "total_items", service.count())).build();
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
        return service.update(id, readCoordinates(body))
                ? Response.noContent().build()
                : notFound(id);
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
            case DELETED -> Response.noContent().build();
            case NOT_FOUND -> notFound(id);
            case IN_USE -> problem(409, "/problems/coordinates-are-in-use",
                    "Coordinates are in use",
                    "The coordinates cannot be deleted while workers reference them.", id);
        };
    }

    private Coordinates readCoordinates(String body) {
        RequestReader.Fields fields = requestReader.read(body).allowOnly("x", "y");
        return fields.validate(new Coordinates(null, fields.int32("x"), fields.int64("y")));
    }

    private CoordinatesResponse toResponse(Coordinates coordinates) {
        return new CoordinatesResponse(coordinates.getId(), coordinates.getX(), coordinates.getY());
    }

    private Response notFound(long id) {
        return problem(404, "/problems/coordinates-not-found", "Coordinates not found",
                "Coordinates with id=" + id + " do not exist.", id);
    }

    private Response problem(int status, String type, String title, String detail, long id) {
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(Map.of("type", type, "title", title, "status", status, "detail", detail,
                        "coordinates_id", id))
                .build();
    }
}
