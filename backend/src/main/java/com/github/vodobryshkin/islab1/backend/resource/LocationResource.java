package com.github.vodobryshkin.islab1.backend.resource;

import com.github.vodobryshkin.islab1.backend.domain.Location;
import com.github.vodobryshkin.islab1.backend.dto.ErrorResponse;
import com.github.vodobryshkin.islab1.backend.dto.IdResponse;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import com.github.vodobryshkin.islab1.backend.dto.LocationRequest;
import com.github.vodobryshkin.islab1.backend.dto.LocationResponse;
import com.github.vodobryshkin.islab1.backend.dto.PageResponse;
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
import java.util.Optional;
import java.util.Set;

/**
 * Операции /locations. Вход и CSRF проверяются до вызова методов.
 */
@Path("/locations")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class LocationResource {
    private static final Set<String> SORT_FIELDS = Set.of("id", "x", "y", "z");
    private static final String NOT_FOUND = "Location with id=%d does not exist.";

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

    /**
     * Нужен CDI для прокси и Jakarta REST, который требует публичный конструктор.
     */
    public LocationResource() {
        this(null, null);
    }

    /**
     * Создаёт локацию.
     *
     * @param body тело запроса
     * @param uriInfo адрес запроса для заголовка Location
     * @return 201 с ID
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String body, @Context UriInfo uriInfo) {
        LocationRequest request = requestReader.read(body, LocationRequest.class);
        request.setId(null);
        Long id = service.create(request.toEntity());

        return Response.created(uriInfo.getAbsolutePathBuilder().path(id.toString()).build())
                .entity(new IdResponse(id))
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
        Optional<Location> location = service.find(id);

        if (location.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(NOT_FOUND.formatted(id)))
                    .build();
        }

        return Response.ok().entity(new LocationResponse(location.get())).build();
    }

    /**
     * Возвращает страницу локаций.
     *
     * @param uriInfo параметры страницы и сортировки
     * @return 200 со страницей
     */
    @GET
    public Response list(@Context UriInfo uriInfo) {
        ListQuery query = requestReader.listQuery(uriInfo.getQueryParameters(), SORT_FIELDS);
        PageResponse<LocationResponse> page = new PageResponse<>(
                service.findPage(query).stream().map(LocationResponse::new).toList(),
                query.getPage(), query.getPageSize(), service.count());

        return Response.ok().entity(page).build();
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

        Location values = requestReader.read(body, LocationRequest.class).toEntity();

        if (!service.update(id, values)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(NOT_FOUND.formatted(id)))
                    .build();
        }

        return Response.noContent().build();
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
            case IN_USE -> Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse("The location cannot be deleted while addresses "
                            + "or persons reference it."))
                    .build();
            default -> Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(NOT_FOUND.formatted(id)))
                    .build();
        };
    }
}
