package com.github.vodobryshkin.islab1.backend.resource;

import com.github.vodobryshkin.islab1.backend.domain.Status;
import com.github.vodobryshkin.islab1.backend.domain.Worker;
import com.github.vodobryshkin.islab1.backend.dto.CountResponse;
import com.github.vodobryshkin.islab1.backend.dto.CreateWorkerResponse;
import com.github.vodobryshkin.islab1.backend.dto.ErrorResponse;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import com.github.vodobryshkin.islab1.backend.dto.PageResponse;
import com.github.vodobryshkin.islab1.backend.dto.WorkerResponse;
import com.github.vodobryshkin.islab1.backend.service.Change;
import com.github.vodobryshkin.islab1.backend.service.SpecialOperationService;
import com.github.vodobryshkin.islab1.backend.service.WorkerService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
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
import java.math.BigInteger;
import java.util.Set;

/**
 * Операции /workers. Вход и CSRF проверяются до вызова методов.
 */
@Path("/workers")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class WorkerResource {
    private static final Set<String> SORT_FIELDS = Set.of("id", "name", "salary", "rating",
            "start_date", "creation_date", "position", "status", "organization_full_name",
            "organization_type", "organization_official_address_street",
            "organization_postal_address_street", "person_eye_color", "person_hair_color",
            "person_nationality");
    private static final String NOT_FOUND = "Worker with id=%d does not exist.";

    private final WorkerService service;
    private final SpecialOperationService specialOperations;
    private final RequestReader requestReader;
    private final RequestEntities entities;

    /**
     * Создаёт ресурс.
     *
     * @param service операции с работниками
     * @param specialOperations специальные операции ТЗ
     * @param requestReader чтение запросов
     * @param entities сборка сущностей из полей запроса
     */
    @Inject
    public WorkerResource(WorkerService service, SpecialOperationService specialOperations,
            RequestReader requestReader, RequestEntities entities) {
        this.service = service;
        this.specialOperations = specialOperations;
        this.requestReader = requestReader;
        this.entities = entities;
    }

    /**
     * Нужен CDI для прокси и Jakarta REST, который требует публичный конструктор.
     */
    public WorkerResource() {
        this(null, null, null, null);
    }

    /**
     * Создаёт работника; новые вложенные объекты создаются в той же транзакции.
     *
     * @param body тело запроса
     * @param request текущий запрос с вошедшим пользователем
     * @param uriInfo адрес запроса для заголовка Location
     * @return 201 с ID работника и организации, 404 при ненайденной ссылке или 409
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String body, @Context HttpServletRequest request,
            @Context UriInfo uriInfo) {
        Worker worker = readWorker(body, false);
        Change change = service.create(worker, request.getUserPrincipal().getName());
        return switch (change.getOutcome()) {
            case DONE -> Response.created(uriInfo.getAbsolutePathBuilder()
                            .path(change.getId().toString()).build())
                    .entity(new CreateWorkerResponse(change.getId().intValue(),
                            worker.getOrganization() == null
                                    ? null
                                    : worker.getOrganization().getId()))
                    .build();
            case CONFLICT -> conflict(change);
            default -> referenceNotFound(change.getSubject(), change.getSubjectId());
        };
    }

    /**
     * Возвращает работника с вложенными объектами.
     *
     * @param rawId ID из пути
     * @return 200 с работником или 404
     */
    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String rawId) {
        int id = requestReader.workerId(rawId);
        return service.find(id)
                .map(worker -> Response.ok().entity(new WorkerResponse(worker)).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse(NOT_FOUND.formatted(id)))
                        .build());
    }

    /**
     * Возвращает страницу работников с фильтрами-подстроками, соединёнными через AND.
     *
     * @param uriInfo параметры страницы, сортировки и фильтры
     * @return 200 со страницей
     */
    @GET
    public Response list(@Context UriInfo uriInfo) {
        MultivaluedMap<String, String> filters = uriInfo.getQueryParameters();
        ListQuery query = requestReader.listQuery(filters, SORT_FIELDS);
        PageResponse<WorkerResponse> page = new PageResponse<>(
                service.findPage(query, filters).stream().map(WorkerResponse::new).toList(),
                query.getPage(), query.getPageSize(), service.count(filters));
        return Response.ok().entity(page).build();
    }

    /**
     * Считает работников с указанным статусом; status=null — работников без статуса.
     *
     * @param uriInfo параметр status
     * @return 200 с количеством или 400 при неверном параметре
     */
    @GET
    @Path("/count_by_status")
    public Response countByStatus(@Context UriInfo uriInfo) {
        String raw = uriInfo.getQueryParameters().getFirst("status");
        Status status = "null".equals(raw) ? null : statusParameter(raw);
        CountResponse count = new CountResponse(specialOperations.countByStatus(status));
        return Response.ok().entity(count).build();
    }

    /**
     * Считает работников со статусом выше заданного; работники без статуса не учитываются.
     *
     * @param uriInfo параметр status
     * @return 200 с количеством или 400 при неверном параметре
     */
    @GET
    @Path("/count_by_status_greater_than")
    public Response countByStatusGreaterThan(@Context UriInfo uriInfo) {
        Status status = statusParameter(uriInfo.getQueryParameters().getFirst("status"));
        CountResponse count =
                new CountResponse(specialOperations.countByStatusGreaterThan(status));
        return Response.ok().entity(count).build();
    }

    /**
     * Возвращает всех работников с рейтингом строго меньше порога, без страниц.
     *
     * @param uriInfo параметр rating
     * @return 200 с массивом работников или 400 при неверном параметре
     */
    @GET
    @Path("/rating_less_than")
    public Response ratingLessThan(@Context UriInfo uriInfo) {
        String raw = uriInfo.getQueryParameters().getFirst("rating");
        if (raw == null) {
            throw requestReader.badRequest("Parameter rating is required.");
        }
        if (!raw.matches("-?\\d+")) {
            throw requestReader.badRequest("Parameter rating must be an integer.");
        }
        if (new BigInteger(raw).bitLength() >= Integer.SIZE) {
            throw requestReader.badRequest("Parameter rating must fit into int32.");
        }
        int rating = Integer.parseInt(raw);
        return Response.ok()
                .entity(specialOperations.findByRatingLessThan(rating).stream()
                        .map(WorkerResponse::new)
                        .toList())
                .build();
    }

    /**
     * Заменяет все поля и привязки работника.
     *
     * @param rawId ID из пути
     * @param body тело запроса
     * @return 204, 404 при отсутствии работника или ссылки, 409 при занятой персоне
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") String rawId, String body) {
        int id = requestReader.workerId(rawId);
        Change change = service.update(id, readWorker(body, true));
        return switch (change.getOutcome()) {
            case DONE -> Response.noContent().build();
            case NOT_FOUND -> Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(NOT_FOUND.formatted(id)))
                    .build();
            case CONFLICT -> conflict(change);
            default -> referenceNotFound(change.getSubject(), change.getSubjectId());
        };
    }

    /**
     * Принимает работника в существующую организацию со статусом HIRED.
     *
     * @param rawId ID из пути
     * @param body тело запроса с organization_id
     * @return 204 или 404 без работника либо организации
     */
    @POST
    @Path("/{id}/hire")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response hire(@PathParam("id") String rawId, String body) {
        int id = requestReader.workerId(rawId);
        RequestFields fields = requestReader.read(body).allowOnly("organization_id");
        long organizationId = fields.validate(fields.id("organization_id"));
        return switch (specialOperations.hire(id, organizationId)) {
            case DONE -> Response.noContent().build();
            case NOT_FOUND -> Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(NOT_FOUND.formatted(id)))
                    .build();
            default -> referenceNotFound("organization", organizationId);
        };
    }

    /**
     * Удаляет работника, если с ним не связана персона.
     *
     * @param rawId ID из пути
     * @return 204, 404 или 409
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String rawId) {
        int id = requestReader.workerId(rawId);
        return switch (service.delete(id)) {
            case DONE -> Response.noContent().build();
            case IN_USE -> Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse("Worker with id=" + id
                            + " is referenced by a person and cannot be deleted."))
                    .build();
            default -> Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(NOT_FOUND.formatted(id)))
                    .build();
        };
    }

    private Status statusParameter(String raw) {
        if (raw == null) {
            throw requestReader.badRequest("Parameter status is required.");
        }
        for (Status status : Status.values()) {
            if (status.name().equals(raw)) {
                return status;
            }
        }
        throw requestReader.badRequest("Parameter status has an unsupported value.");
    }

    private Worker readWorker(String body, boolean update) {
        RequestFields fields = requestReader.read(body);
        return fields.validate(entities.worker(fields, update), "user", "creation_date");
    }

    private Response conflict(Change change) {
        String message = "person".equals(change.getSubject())
                ? "Person with id=" + change.getSubjectId()
                        + " is already assigned to another worker."
                : "Another organization with this full_name already exists.";
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorResponse(message))
                .build();
    }

    private Response referenceNotFound(String subject, long id) {
        String message = switch (subject) {
            case "address" -> "Address with id=%d does not exist.";
            case "coordinates" -> "Coordinates with id=%d do not exist.";
            case "organization" -> "Organization with id=%d does not exist.";
            case "person" -> "Person with id=%d does not exist.";
            default -> "Location with id=%d does not exist.";
        };
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(message.formatted(id)))
                .build();
    }
}
