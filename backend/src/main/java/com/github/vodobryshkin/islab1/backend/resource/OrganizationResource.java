package com.github.vodobryshkin.islab1.backend.resource;

import com.github.vodobryshkin.islab1.backend.domain.Organization;
import com.github.vodobryshkin.islab1.backend.dto.CountResponse;
import com.github.vodobryshkin.islab1.backend.dto.ErrorResponse;
import com.github.vodobryshkin.islab1.backend.dto.IdResponse;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import com.github.vodobryshkin.islab1.backend.dto.OrganizationResponse;
import com.github.vodobryshkin.islab1.backend.dto.PageResponse;
import com.github.vodobryshkin.islab1.backend.service.Change;
import com.github.vodobryshkin.islab1.backend.service.OrganizationService;
import com.github.vodobryshkin.islab1.backend.service.SpecialOperationService;
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
 * Операции /organizations. Вход и CSRF проверяются до вызова методов.
 */
@Path("/organizations")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class OrganizationResource {
    private static final Set<String> SORT_FIELDS = Set.of("id", "full_name", "annual_turnover",
            "employees_count", "rating", "type", "official_address_id", "postal_address_id",
            "official_address_street", "postal_address_street");
    private static final String NOT_FOUND = "Organization with id=%d does not exist.";
    private static final String NAME_TAKEN =
            "Another organization with this full_name already exists.";

    private final OrganizationService service;
    private final SpecialOperationService specialOperations;
    private final RequestReader requestReader;
    private final RequestEntities entities;

    /**
     * Создаёт ресурс.
     *
     * @param service операции с организациями
     * @param specialOperations специальные операции ТЗ
     * @param requestReader чтение запросов
     * @param entities сборка сущностей из полей запроса
     */
    @Inject
    public OrganizationResource(OrganizationService service,
            SpecialOperationService specialOperations, RequestReader requestReader,
            RequestEntities entities) {
        this.service = service;
        this.specialOperations = specialOperations;
        this.requestReader = requestReader;
        this.entities = entities;
    }

    /**
     * Нужен CDI для прокси и Jakarta REST, который требует публичный конструктор.
     */
    public OrganizationResource() {
        this(null, null, null, null);
    }

    /**
     * Создаёт организацию; новые адреса и локации создаются в той же транзакции.
     *
     * @param body тело запроса
     * @param uriInfo адрес запроса для заголовка Location
     * @return 201 с ID, 404 при ненайденной ссылке или 409 при занятом названии
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String body, @Context UriInfo uriInfo) {
        Change change = service.create(readOrganization(body));
        return switch (change.getOutcome()) {
            case DONE -> Response.created(uriInfo.getAbsolutePathBuilder()
                            .path(change.getId().toString()).build())
                    .entity(new IdResponse(change.getId()))
                    .build();
            case CONFLICT -> Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse(NAME_TAKEN))
                    .build();
            default -> referenceNotFound(change);
        };
    }

    /**
     * Возвращает организацию с адресами и их локациями.
     *
     * @param rawId ID из пути
     * @return 200 с организацией или 404
     */
    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String rawId) {
        long id = requestReader.id(rawId);
        Optional<Organization> organization = service.find(id);
        if (organization.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(NOT_FOUND.formatted(id)))
                    .build();
        }
        return Response.ok().entity(new OrganizationResponse(organization.get())).build();
    }

    /**
     * Возвращает страницу организаций с фильтрами-подстроками, соединёнными через AND.
     *
     * @param uriInfo параметры страницы, сортировки и фильтры
     * @return 200 со страницей
     */
    @GET
    public Response list(@Context UriInfo uriInfo) {
        MultivaluedMap<String, String> filters = uriInfo.getQueryParameters();
        ListQuery query = requestReader.listQuery(filters, SORT_FIELDS);
        PageResponse<OrganizationResponse> page = new PageResponse<>(
                service.findPage(query, filters).stream().map(OrganizationResponse::new).toList(),
                query.getPage(), query.getPageSize(), service.count(filters));
        return Response.ok().entity(page).build();
    }

    /**
     * Заменяет все поля и адреса организации.
     *
     * @param rawId ID из пути
     * @param body тело запроса
     * @return 204, 404 при отсутствии организации или ссылки, 409 при занятом названии
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") String rawId, String body) {
        long id = requestReader.id(rawId);
        Change change = service.update(id, readOrganization(body));
        return switch (change.getOutcome()) {
            case DONE -> Response.noContent().build();
            case NOT_FOUND -> Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(NOT_FOUND.formatted(id)))
                    .build();
            case CONFLICT -> Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse(NAME_TAKEN))
                    .build();
            default -> referenceNotFound(change);
        };
    }

    /**
     * Умножает зарплаты всех работников организации на положительный коэффициент.
     *
     * @param rawId ID из пути
     * @param body тело запроса с coefficient
     * @return 200 с числом работников, 404 без организации или 409, если новая зарплата
     *     вышла бы за допустимые пределы
     */
    @POST
    @Path("/{id}/index_salaries")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response indexSalaries(@PathParam("id") String rawId, String body) {
        long id = requestReader.id(rawId);
        RequestFields fields = requestReader.read(body).allowOnly("coefficient");
        Double coefficient = fields.float64("coefficient");
        if (coefficient != null && coefficient <= 0) {
            fields.reject("coefficient", "MUST_BE_POSITIVE",
                    "coefficient must be greater than zero.");
        }
        Change change = specialOperations.indexSalaries(id, fields.validate(coefficient));
        return switch (change.getOutcome()) {
            case DONE -> Response.ok().entity(new CountResponse(change.getId())).build();
            case CONFLICT -> Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse(
                            "Indexation would produce a salary outside the allowed range."))
                    .build();
            default -> Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(NOT_FOUND.formatted(id)))
                    .build();
        };
    }

    /**
     * Удаляет организацию, если на неё не ссылаются работники; адреса остаются.
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
                            "The organization cannot be deleted while workers reference it."))
                    .build();
            default -> Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(NOT_FOUND.formatted(id)))
                    .build();
        };
    }

    private Organization readOrganization(String body) {
        RequestFields fields = requestReader.read(body);
        return fields.validate(entities.organization(fields));
    }

    private Response referenceNotFound(Change change) {
        String message = "address".equals(change.getSubject())
                ? "Address with id=%d does not exist."
                : "Location with id=%d does not exist.";
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(message.formatted(change.getSubjectId())))
                .build();
    }
}
