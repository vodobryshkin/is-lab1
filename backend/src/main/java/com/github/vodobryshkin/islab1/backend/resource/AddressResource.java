package com.github.vodobryshkin.islab1.backend.resource;

import com.github.vodobryshkin.islab1.backend.domain.Address;
import com.github.vodobryshkin.islab1.backend.domain.Location;
import com.github.vodobryshkin.islab1.backend.dto.AddressResponse;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import com.github.vodobryshkin.islab1.backend.dto.LocationResponse;
import com.github.vodobryshkin.islab1.backend.dto.ProblemDetails;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Операции {@code /addresses}. Вход и CSRF проверяются до вызова методов.
 */
@Path("/addresses")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class AddressResource {
    private static final String STREET = "street";
    private static final Set<String> SORT_FIELDS = Set.of("id", STREET);

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

    /** Нужен CDI для прокси и Jakarta REST, который требует публичный конструктор. */
    public AddressResource() {
        this(null, null);
    }

    /**
     * Создаёт адрес вместе с новой локацией или со ссылкой на существующую.
     *
     * @param body тело запроса
     * @param uriInfo адрес запроса для заголовка {@code Location}
     * @return 201 с ID или 404, если локации нет
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String body, @Context UriInfo uriInfo) {
        Address address = readAddress(body);
        Optional<Long> id = service.create(address);
        if (id.isEmpty()) {
            return townNotFound(address);
        }
        return Response.created(uriInfo.getAbsolutePathBuilder().path(id.get().toString()).build())
                .entity(Map.of("id", id.get()))
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
        return service.find(id)
                .map(address -> Response.ok(toResponse(address)).build())
                .orElseGet(() -> notFound(id));
    }

    /**
     * Возвращает страницу адресов с фильтром по подстроке улицы.
     *
     * @param uriInfo параметры страницы, сортировки и фильтр {@code street}
     * @return 200 со страницей
     */
    @GET
    public Response list(@Context UriInfo uriInfo) {
        ListQuery query = requestReader.listQuery(uriInfo.getQueryParameters(), SORT_FIELDS);
        String street = uriInfo.getQueryParameters().getFirst(STREET);
        List<AddressResponse> items =
                service.findPage(query, street).stream().map(this::toResponse).toList();
        return Response.ok(Map.of("items", items, "page", query.page(),
                "page_size", query.pageSize(), "total_items", service.count(street))).build();
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
        Address values = readAddress(body);
        return switch (service.update(id, values)) {
            case DONE -> Response.noContent().build();
            case TOWN_NOT_FOUND -> townNotFound(values);
            default -> notFound(id);
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
            case IN_USE -> Response.status(409).type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(Map.of("type", "/problems/address-is-in-use",
                            "title", "Address is in use", "status", 409,
                            "detail", "The address cannot be deleted while organizations "
                                    + "reference it.", "address_id", id))
                    .build();
            default -> notFound(id);
        };
    }

    /**
     * Читает адрес. Город — новая локация {@code {x, y, z}}, ссылка {@code {id}} или
     * {@code null}; у ссылки в возвращаемом адресе задан только ID локации.
     */
    private Address readAddress(String body) {
        RequestReader.Fields fields = requestReader.read(body).allowOnly(STREET, "town");
        String street = fields.string(STREET);
        RequestReader.Fields town = fields.object("town");
        if (town == null) {
            return fields.validate(new Address(null, street, null));
        }
        town.allowOnly("id", "x", "y", "z");
        if (!town.has("id")) {
            return fields.validate(new Address(null, street,
                    new Location(null, town.int32("x"), town.int32("y"), town.int64("z"))));
        }
        if (town.has("x") || town.has("y") || town.has("z")) {
            town.reject("id", "MUTUALLY_EXCLUSIVE",
                    "town must contain either id or x, y and z, not both.");
        }
        Long townId = town.id("id");
        Address address = fields.validate(new Address(null, street, null));
        address.setTown(new Location(townId, null, null, null));
        return address;
    }

    private AddressResponse toResponse(Address address) {
        Location town = address.getTown();
        return new AddressResponse(address.getId(), address.getStreet(), town == null
                ? null
                : new LocationResponse(town.getId(), town.getX(), town.getY(), town.getZ()));
    }

    private Response notFound(long id) {
        return new ProblemDetails(404, "/problems/address-not-found", "Address not found",
                "Address with id=" + id + " does not exist.").toResponse();
    }

    private Response townNotFound(Address address) {
        long locationId = address.getTown().getId();
        return Response.status(404).type(MediaType.APPLICATION_JSON_TYPE)
                .entity(Map.of("type", "/problems/location-not-found",
                        "title", "Location not found", "status", 404,
                        "detail", "Location with id=" + locationId + " does not exist.",
                        "location_id", locationId))
                .build();
    }
}
