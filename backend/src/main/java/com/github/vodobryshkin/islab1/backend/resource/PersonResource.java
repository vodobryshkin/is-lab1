package com.github.vodobryshkin.islab1.backend.resource;

import com.github.vodobryshkin.islab1.backend.domain.Person;
import com.github.vodobryshkin.islab1.backend.dto.ErrorResponse;
import com.github.vodobryshkin.islab1.backend.dto.IdResponse;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import com.github.vodobryshkin.islab1.backend.dto.PageResponse;
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
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.util.Optional;
import java.util.Set;

/**
 * Операции /persons. Вход и CSRF проверяются до вызова методов.
 */
@Path("/persons")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {
    private static final Set<String> SORT_FIELDS =
            Set.of("id", "eye_color", "hair_color", "nationality", "birthday", "weight");
    private static final String NOT_FOUND = "Person with id=%d does not exist.";
    private static final String LOCATION_NOT_FOUND = "Location with id=%d does not exist.";

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
     * @param uriInfo адрес запроса для заголовка Location
     * @return 201 с ID или 404, если локации нет
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String body, @Context UriInfo uriInfo) {
        Person person = readPerson(body);
        Long locationId = person.getLocation().getId();
        Optional<Long> id = service.create(person);
        if (id.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(LOCATION_NOT_FOUND.formatted(locationId)))
                    .build();
        }
        return Response.created(uriInfo.getAbsolutePathBuilder().path(id.get().toString()).build())
                .entity(new IdResponse(id.get()))
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
        Optional<Person> person = service.find(id);
        if (person.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(NOT_FOUND.formatted(id)))
                    .build();
        }
        return Response.ok().entity(new PersonResponse(person.get())).build();
    }

    /**
     * Возвращает страницу персон с фильтрами-подстроками, соединёнными через AND.
     *
     * @param uriInfo параметры страницы, сортировки и фильтры
     * @return 200 со страницей
     */
    @GET
    public Response list(@Context UriInfo uriInfo) {
        MultivaluedMap<String, String> filters = uriInfo.getQueryParameters();
        ListQuery query = requestReader.listQuery(filters, SORT_FIELDS);
        PageResponse<PersonResponse> page = new PageResponse<>(
                service.findPage(query, filters).stream().map(PersonResponse::new).toList(),
                query.getPage(), query.getPageSize(), service.count(filters));
        return Response.ok().entity(page).build();
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
            case REFERENCE_NOT_FOUND -> Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(LOCATION_NOT_FOUND.formatted(locationId)))
                    .build();
            default -> Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(NOT_FOUND.formatted(id)))
                    .build();
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
            case IN_USE -> Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse(
                            "The person cannot be deleted while a worker references it."))
                    .build();
            default -> Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(NOT_FOUND.formatted(id)))
                    .build();
        };
    }

    private Person readPerson(String body) {
        RequestFields fields = requestReader.read(body);
        return fields.validate(entities.person(fields));
    }
}
