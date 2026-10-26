package com.github.vodobryshkin.islab1.backend.resource;

import com.github.vodobryshkin.islab1.backend.domain.Organization;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import com.github.vodobryshkin.islab1.backend.dto.OrganizationResponse;
import com.github.vodobryshkin.islab1.backend.dto.ProblemDetails;
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
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Операции {@code /organizations}. Вход и CSRF проверяются до вызова методов.
 */
@Path("/organizations")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class OrganizationResource {
    private static final Set<String> SORT_FIELDS = Set.of("id", "full_name", "annual_turnover",
            "employees_count", "rating", "type", "official_address_id", "postal_address_id",
            "official_address_street", "postal_address_street");

    private static final String ADDRESS = "address";
    private static final String COEFFICIENT = "coefficient";

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

    /** Нужен CDI для прокси и Jakarta REST, который требует публичный конструктор. */
    public OrganizationResource() {
        this(null, null, null, null);
    }

    /**
     * Создаёт организацию; новые адреса и локации создаются в той же транзакции.
     *
     * @param body тело запроса
     * @param uriInfo адрес запроса для заголовка {@code Location}
     * @return 201 с ID, 404 при ненайденной ссылке или 409 при занятом названии
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String body, @Context UriInfo uriInfo) {
        Organization organization = readOrganization(body);
        Change change = service.create(organization);
        return switch (change.outcome()) {
            case DONE -> Response.created(
                            uriInfo.getAbsolutePathBuilder().path(change.id().toString()).build())
                    .entity(Map.of("id", change.id()))
                    .build();
            case CONFLICT -> nameTaken(organization);
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
        return service.find(id)
                .map(organization -> Response.ok(new OrganizationResponse(organization)).build())
                .orElseGet(() -> notFound(id));
    }

    /**
     * Возвращает страницу организаций с фильтрами-подстроками, соединёнными через AND.
     *
     * @param uriInfo параметры страницы, сортировки и фильтры
     * @return 200 со страницей
     */
    @GET
    public Response list(@Context UriInfo uriInfo) {
        Map<String, List<String>> filters = uriInfo.getQueryParameters();
        ListQuery query = requestReader.listQuery(uriInfo.getQueryParameters(), SORT_FIELDS);
        List<OrganizationResponse> items =
                service.findPage(query, filters).stream().map(OrganizationResponse::new).toList();
        return Response.ok(Map.of("items", items, "page", query.page(),
                "page_size", query.pageSize(), "total_items", service.count(filters))).build();
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
        Organization values = readOrganization(body);
        Change change = service.update(id, values);
        return switch (change.outcome()) {
            case DONE -> Response.noContent().build();
            case NOT_FOUND -> notFound(id);
            case CONFLICT -> nameTaken(values);
            default -> referenceNotFound(change);
        };
    }

    /**
     * Умножает зарплаты всех работников организации на положительный коэффициент.
     *
     * @param rawId ID из пути
     * @param body тело запроса с {@code coefficient}
     * @return 200 с числом работников, 404 без организации или 409, если новая зарплата
     *     вышла бы за допустимые пределы
     */
    @POST
    @Path("/{id}/index_salaries")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response indexSalaries(@PathParam("id") String rawId, String body) {
        long id = requestReader.id(rawId);
        RequestFields fields = requestReader.read(body).allowOnly(COEFFICIENT);
        Double coefficient = fields.float64(COEFFICIENT);
        if (coefficient != null && coefficient <= 0) {
            fields.reject(COEFFICIENT, "MUST_BE_POSITIVE",
                    "Coefficient must be greater than zero.");
        }
        Change change = specialOperations.indexSalaries(id, fields.validate(coefficient));
        return switch (change.outcome()) {
            case DONE -> Response.ok(Map.of("count", change.id())).build();
            case CONFLICT -> problem(409, "/problems/salary-indexation-conflict",
                    "Salary indexation conflict",
                    "Indexation would produce a salary outside the allowed range.",
                    "organization_id", id);
            default -> notFound(id);
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
            case IN_USE -> problem(409, "/problems/organization-has-workers",
                    "Organization cannot be deleted",
                    "The organization cannot be deleted while workers reference it.",
                    "organization_id", id);
            default -> notFound(id);
        };
    }

    private Organization readOrganization(String body) {
        RequestFields fields = requestReader.read(body);
        return fields.validate(entities.organization(fields));
    }

    /** У {@code address-not-found} в контракте нет поля с ID, у локации оно есть. */
    private Response referenceNotFound(Change change) {
        if (ADDRESS.equals(change.subject())) {
            return new ProblemDetails(404, "/problems/address-not-found", "Address not found",
                    "Address with id=" + change.subjectId() + " does not exist.").toResponse();
        }
        return problem(404, "/problems/location-not-found", "Location not found",
                "Location with id=" + change.subjectId() + " does not exist.", "location_id",
                change.subjectId());
    }

    private Response nameTaken(Organization organization) {
        return Response.status(409)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(Map.of("type", "/problems/organization-name-already-exists",
                        "title", "Organization name already exists", "status", 409,
                        "detail", "Another organization with this full_name already exists.",
                        "full_name", organization.getFullName()))
                .build();
    }

    private Response notFound(long id) {
        return problem(404, "/problems/organization-not-found", "Organization not found",
                "Organization with id=" + id + " does not exist.", "organization_id", id);
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
