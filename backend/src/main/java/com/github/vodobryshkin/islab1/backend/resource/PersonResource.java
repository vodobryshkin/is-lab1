package com.github.vodobryshkin.islab1.backend.resource;

import com.github.vodobryshkin.islab1.backend.domain.Person;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import com.github.vodobryshkin.islab1.backend.dto.LocationResponse;
import com.github.vodobryshkin.islab1.backend.dto.PersonResponse;
import com.github.vodobryshkin.islab1.backend.service.PersonService;
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
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Операции {@code /persons}. Вход и CSRF проверяются до вызова методов.
 */
@Path("/persons")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {
    private static final String EYE_COLOR = "eye_color";
    private static final String HAIR_COLOR = "hair_color";
    private static final String NATIONALITY = "nationality";
    private static final Set<String> SORT_FIELDS =
            Set.of("id", EYE_COLOR, HAIR_COLOR, NATIONALITY, "birthday", "weight");

    private final PersonService service;
    private final RequestReader requestReader;
    private final RequestEntities entities;

    /**
     * Создаёт ресурс.
     *
     * @param service операции с персонами
     * @param requestReader чтение запросов
     * @param entities сборка сущностей из полей запроса
     */
    @Inject
    public PersonResource(PersonService service, RequestReader requestReader,
            RequestEntities entities) {
        this.service = service;
        this.requestReader = requestReader;
        this.entities = entities;
    }

    /** Нужен CDI для прокси и Jakarta REST, который требует публичный конструктор. */
    public PersonResource() {
        this(null, null, null);
    }

    /**
     * Создаёт персону без работника с новой или существующей локацией.
     *
     * @param body тело запроса
     * @param uriInfo адрес запроса для заголовка {@code Location}
     * @return 201 с ID или 404, если локации нет
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String body, @Context UriInfo uriInfo) {
        Person person = readPerson(body);
        Long locationId = person.getLocation().getId();
        Optional<Long> id = service.create(person);
        if (id.isEmpty()) {
            return problem(404, "/problems/location-not-found", "Location not found",
                    "Location with id=" + locationId + " does not exist.", "location_id",
                    locationId);
        }
        return Response.created(uriInfo.getAbsolutePathBuilder().path(id.get().toString()).build())
                .entity(Map.of("id", id.get()))
                .build();
    }

    /**
     * Возвращает персону с локацией.
     *
     * @param rawId ID из пути
     * @return 200 с персоной или 404
     */
    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String rawId) {
        long id = requestReader.id(rawId);
        return service.find(id)
                .map(person -> Response.ok(toResponse(person)).build())
                .orElseGet(() -> notFound(id));
    }

    /**
     * Возвращает страницу персон с фильтрами-подстроками, соединёнными через AND.
     *
     * @param uriInfo параметры страницы, сортировки и фильтры
     * @return 200 со страницей
     */
    @GET
    public Response list(@Context UriInfo uriInfo) {
        Map<String, List<String>> filters = uriInfo.getQueryParameters();
        ListQuery query = requestReader.listQuery(uriInfo.getQueryParameters(), SORT_FIELDS);
        List<PersonResponse> items =
                service.findPage(query, filters).stream().map(this::toResponse).toList();
        return Response.ok(Map.of("items", items, "page", query.page(),
                "page_size", query.pageSize(), "total_items", service.count(filters))).build();
    }

    /**
     * Заменяет поля и локацию персоны; связь с работником сохраняется.
     *
     * @param rawId ID из пути
     * @param body тело запроса
     * @return 204 или 404, если нет персоны или локации
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") String rawId, String body) {
        long id = requestReader.id(rawId);
        Person values = readPerson(body);
        Long locationId = values.getLocation().getId();
        return switch (service.update(id, values)) {
            case DONE -> Response.noContent().build();
            case REFERENCE_NOT_FOUND -> problem(404, "/problems/location-not-found",
                    "Location not found", "Location with id=" + locationId + " does not exist.",
                    "location_id", locationId);
            default -> notFound(id);
        };
    }

    /**
     * Удаляет персону, если она не связана с работником.
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
            case IN_USE -> problem(409, "/problems/person-is-in-use", "Person is in use",
                    "The person cannot be deleted while a worker references it.", "person_id", id);
            default -> notFound(id);
        };
    }

    private Person readPerson(String body) {
        RequestFields fields = requestReader.read(body);
        return fields.validate(entities.person(fields));
    }

    /** Дата рождения отдаётся в UTC, как {@code utcDateTime} контракта. */
    private PersonResponse toResponse(Person person) {
        return new PersonResponse(person.getId(), person.getEyeColor(), person.getHairColor(),
                new LocationResponse(person.getLocation()),
                person.getBirthday().withZoneSameInstant(ZoneOffset.UTC), person.getWeight(),
                person.getNationality());
    }

    private Response notFound(long id) {
        return problem(404, "/problems/person-not-found", "Person not found",
                "Person with id=" + id + " does not exist.", "person_id", id);
    }

    private Response problem(int status, String type, String title, String detail,
            String idField, long id) {
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(Map.of("type", type, "title", title, "status", status, "detail", detail,
                        idField, id))
                .build();
    }
}
