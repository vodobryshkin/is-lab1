package com.github.vodobryshkin.islab1.backend.resource;

import com.github.vodobryshkin.islab1.backend.domain.Address;
import com.github.vodobryshkin.islab1.backend.dto.AddressRequest;
import com.github.vodobryshkin.islab1.backend.dto.AddressResponse;
import com.github.vodobryshkin.islab1.backend.dto.ErrorResponse;
import com.github.vodobryshkin.islab1.backend.dto.IdResponse;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import com.github.vodobryshkin.islab1.backend.dto.PageResponse;
import com.github.vodobryshkin.islab1.backend.service.AddressService;
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
 * Операции /addresses. Вход и CSRF проверяются до вызова методов.
 */
@Path("/addresses")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class AddressResource {
    private static final String STREET = "street";
    private static final Set<String> SORT_FIELDS = Set.of("id", STREET);
    private static final String NOT_FOUND = "Address with id=%d does not exist.";
    private static final String TOWN_NOT_FOUND = "Location with id=%d does not exist.";

    private final AddressService service;
    private final RequestReader requestReader;

    /**
     * Создаёт ресурс.
     *
     * @param service операции с адресами
     * @param requestReader чтение запросов
     */
    @Inject
    public AddressResource(AddressService service, RequestReader requestReader) {
        this.service = service;
        this.requestReader = requestReader;
    }

    /**
     * Нужен CDI для прокси и Jakarta REST, который требует публичный конструктор.
     */
    public AddressResource() {
        this(null, null);
    }

    /**
     * Создаёт адрес вместе с новой локацией или со ссылкой на существующую.
     *
     * @param body тело запроса
     * @param uriInfo адрес запроса для заголовка Location
     * @return 201 с ID или 404, если локации нет
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String body, @Context UriInfo uriInfo) {
        Address address = requestReader.read(body, AddressRequest.class).toEntity();
        Optional<Long> id = service.create(address);

        if (id.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(TOWN_NOT_FOUND.formatted(address.getTown().getId())))
                    .build();
        }

        return Response.created(uriInfo.getAbsolutePathBuilder().path(id.get().toString()).build())
                .entity(new IdResponse(id.get()))
                .build();
    }

    /**
     * Возвращает адрес с вложенной локацией.
     *
     * @param rawId ID из пути
     * @return 200 с адресом или 404
     */
    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String rawId) {
        long id = requestReader.id(rawId);
        Optional<Address> address = service.find(id);

        if (address.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(NOT_FOUND.formatted(id)))
                    .build();
        }

        return Response.ok().entity(new AddressResponse(address.get())).build();
    }

    /**
     * Возвращает страницу адресов с фильтром по подстроке улицы.
     *
     * @param uriInfo параметры страницы, сортировки и фильтр street
     * @return 200 со страницей
     */
    @GET
    public Response list(@Context UriInfo uriInfo) {
        ListQuery query = requestReader.listQuery(uriInfo.getQueryParameters(), SORT_FIELDS);
        String street = uriInfo.getQueryParameters().getFirst(STREET);
        PageResponse<AddressResponse> page = new PageResponse<>(
                service.findPage(query, street).stream().map(AddressResponse::new).toList(),
                query.getPage(), query.getPageSize(), service.count(street));

        return Response.ok().entity(page).build();
    }

    /**
     * Заменяет улицу и город адреса.
     *
     * @param rawId ID из пути
     * @param body тело запроса
     * @return 204 или 404, если нет адреса или локации
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") String rawId, String body) {
        long id = requestReader.id(rawId);
        Address values = requestReader.read(body, AddressRequest.class).toEntity();

        return switch (service.update(id, values)) {
            case DONE -> Response.noContent().build();
            case REFERENCE_NOT_FOUND -> Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(TOWN_NOT_FOUND.formatted(values.getTown().getId())))
                    .build();
            default -> Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(NOT_FOUND.formatted(id)))
                    .build();
        };
    }

    /**
     * Удаляет адрес, если его не используют организации.
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
                            "The address cannot be deleted while organizations reference it."))
                    .build();
            default -> Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(NOT_FOUND.formatted(id)))
                    .build();
        };
    }
}
