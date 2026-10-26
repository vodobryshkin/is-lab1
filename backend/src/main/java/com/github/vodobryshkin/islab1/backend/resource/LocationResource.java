package com.github.vodobryshkin.islab1.backend.resource;

import com.github.vodobryshkin.islab1.backend.domain.Location;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import com.github.vodobryshkin.islab1.backend.dto.LocationResponse;
import com.github.vodobryshkin.islab1.backend.dto.ProblemDetails;
import com.github.vodobryshkin.islab1.backend.service.LocationService;
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
 * Операции {@code /locations}. Вход и CSRF проверяются до вызова методов.
 */
@Path("/locations")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class LocationResource {
    private static final Set<String> SORT_FIELDS = Set.of("id", "x", "y", "z");

    private final LocationService service;
    private final RequestReader requestReader;

    /**
     * Создаёт ресурс.
     *
     * @param service операции с локациями
     * @param requestReader чтение запросов
     */
    @Inject
    public LocationResource(LocationService service, RequestReader requestReader) {
        this.service = service;
        this.requestReader = requestReader;
    }

    /** Нужен CDI для прокси и Jakarta REST, который требует публичный конструктор. */
    public LocationResource() {
        this(null, null);
    }

    /**
     * Создаёт локацию.
     *
     * @param body тело запроса
     * @param uriInfo адрес запроса для заголовка {@code Location}
     * @return 201 с ID
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String body, @Context UriInfo uriInfo) {
        Long id = service.create(readLocation(body));
        return Response.created(uriInfo.getAbsolutePathBuilder().path(id.toString()).build())
                .entity(Map.of("id", id))
                .build();
    }

    /**
     * Возвращает локацию.
     *
     * @param rawId ID из пути
     * @return 200 с локацией или 404
     */
    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String rawId) {
        long id = requestReader.id(rawId);
        return service.find(id)
                .map(location -> Response.ok(toResponse(location)).build())
                .orElseGet(() -> notFound(id));
    }

    /**
     * Возвращает страницу локаций.
     *
     * @param uriInfo параметры {@code page}, {@code page_size}, {@code sort_by}, {@code sort_order}
     * @return 200 со страницей
     */
    @GET
    public Response list(@Context UriInfo uriInfo) {
        ListQuery query = requestReader.listQuery(uriInfo.getQueryParameters(), SORT_FIELDS);
        List<LocationResponse> items =
                service.findPage(query).stream().map(this::toResponse).toList();
        return Response.ok(Map.of("items", items, "page", query.page(),
                "page_size", query.pageSize(), "total_items", service.count())).build();
    }

    /**
     * Заменяет все поля локации.
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
        return service.update(id, readLocation(body))
                ? Response.noContent().build()
                : notFound(id);
    }

    /**
     * Удаляет локацию, если на неё не ссылаются адреса и персоны.
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
            case IN_USE -> problem(409, "/problems/location-is-in-use", "Location is in use",
                    "The location cannot be deleted while addresses or persons reference it.", id);
            default -> notFound(id);
        };
    }

    private Location readLocation(String body) {
        RequestFields fields = requestReader.read(body).allowOnly("x", "y", "z");
        return fields.validate(
                new Location(null, fields.int32("x"), fields.int32("y"), fields.int64("z")));
    }

    private LocationResponse toResponse(Location location) {
        return new LocationResponse(location.getId(), location.getX(), location.getY(),
                location.getZ());
    }

    private Response notFound(long id) {
        return problem(404, "/problems/location-not-found", "Location not found",
                "Location with id=" + id + " does not exist.", id);
    }

    private Response problem(int status, String type, String title, String detail, long id) {
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(Map.of("type", type, "title", title, "status", status, "detail", detail,
                        "location_id", id))
                .build();
    }
}
