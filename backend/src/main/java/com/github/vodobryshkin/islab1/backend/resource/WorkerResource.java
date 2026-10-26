package com.github.vodobryshkin.islab1.backend.resource;

import com.github.vodobryshkin.islab1.backend.domain.Status;
import com.github.vodobryshkin.islab1.backend.domain.Worker;
import com.github.vodobryshkin.islab1.backend.dto.ListQuery;
import com.github.vodobryshkin.islab1.backend.dto.ProblemDetails;
import com.github.vodobryshkin.islab1.backend.dto.WorkerResponse;
import com.github.vodobryshkin.islab1.backend.service.Change;
import com.github.vodobryshkin.islab1.backend.service.SpecialOperationService;
import com.github.vodobryshkin.islab1.backend.service.WorkerService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Операции {@code /workers}. Вход и CSRF проверяются до вызова методов.
 */
@Path("/workers")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class WorkerResource {
    private static final String STATUS = "status";
    private static final Set<String> SORT_FIELDS = Set.of("id", "name", "salary", "rating",
            "start_date", "creation_date", "position", STATUS, "organization_full_name",
            "organization_type", "organization_official_address_street",
            "organization_postal_address_street", "person_eye_color", "person_hair_color",
            "person_nationality");

    private static final String ORGANIZATION = "organization";
    private static final String PERSON = "person";
    private static final String MISSING = " does not exist.";
    private static final String RATING_PARAMETER = "rating";
    private static final String COUNT = "count";
    private static final String NOT_INT32 = "Rating must be an int32 value.";
    private static final String REQUIRED = "REQUIRED";
    private static final String NULL = "null";

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

    /** Нужен CDI для прокси и Jakarta REST, который требует публичный конструктор. */
    public WorkerResource() {
        this(null, null, null, null);
    }

    /**
     * Создаёт работника; новые вложенные объекты создаются в той же транзакции.
     * Создатель — вошедший пользователь.
     *
     * @param body тело запроса
     * @param request текущий запрос с principal
     * @param uriInfo адрес запроса для заголовка {@code Location}
     * @return 201 с ID работника и организации, 404 при ненайденной ссылке или 409
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String body, @Context HttpServletRequest request,
            @Context UriInfo uriInfo) {
        Worker worker = readWorker(body, false);
        Change change = service.create(worker, request.getUserPrincipal().getName());
        return switch (change.outcome()) {
            case DONE -> Response.created(
                            uriInfo.getAbsolutePathBuilder().path(change.id().toString()).build())
                    .entity(new Created(change.id(), worker.getOrganization() == null
                            ? null
                            : worker.getOrganization().getId()))
                    .build();
            case CONFLICT -> conflict(change, worker);
            default -> referenceNotFound(change);
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
                .map(worker -> Response.ok(new WorkerResponse(worker)).build())
                .orElseGet(() -> notFound(id));
    }

    /**
     * Возвращает страницу работников с фильтрами-подстроками, соединёнными через AND.
     *
     * @param uriInfo параметры страницы, сортировки и фильтры
     * @return 200 со страницей
     */
    @GET
    public Response list(@Context UriInfo uriInfo) {
        Map<String, List<String>> filters = uriInfo.getQueryParameters();
        ListQuery query = requestReader.listQuery(uriInfo.getQueryParameters(), SORT_FIELDS);
        List<WorkerResponse> items =
                service.findPage(query, filters).stream().map(WorkerResponse::new).toList();
        return Response.ok(Map.of("items", items, "page", query.page(),
                "page_size", query.pageSize(), "total_items", service.count(filters))).build();
    }

    /**
     * Считает работников с указанным статусом; {@code status=null} — без статуса.
     *
     * @param uriInfo параметр {@code status}
     * @return 200 с количеством или 400 при неверном параметре
     */
    @GET
    @Path("/count_by_status")
    public Response countByStatus(@Context UriInfo uriInfo) {
        String raw = uriInfo.getQueryParameters().getFirst(STATUS);
        Status status = NULL.equals(raw) ? null : statusParameter(raw);
        return Response.ok(Map.of(COUNT, specialOperations.countByStatus(status))).build();
    }

    /**
     * Считает работников со статусом выше заданного; работники без статуса не учитываются.
     *
     * @param uriInfo параметр {@code status}
     * @return 200 с количеством или 400 при неверном параметре
     */
    @GET
    @Path("/count_by_status_greater_than")
    public Response countByStatusGreaterThan(@Context UriInfo uriInfo) {
        Status status =
                statusParameter(uriInfo.getQueryParameters().getFirst(STATUS));
        return Response.ok(Map.of(COUNT, specialOperations.countByStatusGreaterThan(status)))
                .build();
    }

    /**
     * Возвращает всех работников с рейтингом строго меньше порога, без страниц.
     *
     * @param uriInfo параметр {@code rating}
     * @return 200 с массивом работников или 400 при неверном параметре
     */
    @GET
    @Path("/rating_less_than")
    public Response ratingLessThan(@Context UriInfo uriInfo) {
        int rating = ratingParameter(uriInfo.getQueryParameters().getFirst(RATING_PARAMETER));
        List<WorkerResponse> workers = specialOperations.findByRatingLessThan(rating).stream()
                .map(WorkerResponse::new)
                .toList();
        return Response.ok(workers).build();
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
        Worker values = readWorker(body, true);
        Change change = service.update(id, values);
        return switch (change.outcome()) {
            case DONE -> Response.noContent().build();
            case NOT_FOUND -> notFound(id);
            case CONFLICT -> conflict(change, values);
            default -> referenceNotFound(change);
        };
    }

    /**
     * Удаляет работника, если на него не ссылается персона.
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
            case IN_USE -> problem(409, "/problems/worker-has-linked-objects",
                    "Worker has linked objects",
                    "Worker with id=" + id + " is referenced by a person and cannot be deleted.",
                    "worker_id", id);
            default -> notFound(id);
        };
    }

    /** Ответ создания; {@code organization_id} равен {@code null} без организации. */
    public record Created(long id,
            @JsonbNillable @JsonbProperty("organization_id") Long organizationId) {}

    private Status statusParameter(String raw) {
        if (raw == null) {
            throw invalidParameter(STATUS, REQUIRED, "status is required.");
        }
        for (Status status : Status.values()) {
            if (status.name().equals(raw)) {
                return status;
            }
        }
        throw invalidParameter(STATUS, "INVALID_ENUM_VALUE",
                "The status value is not supported by this operation.");
    }

    /** Целое вне диапазона int32 — {@code OUT_OF_RANGE}, остальное нечисловое — тип. */
    private int ratingParameter(String raw) {
        if (raw == null) {
            throw invalidParameter(RATING_PARAMETER, REQUIRED, "rating is required.");
        }
        if (!raw.matches("-?\\d+")) {
            throw invalidParameter(RATING_PARAMETER, "INVALID_TYPE", NOT_INT32);
        }
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException exception) {
            throw new WebApplicationException(exception,
                    invalidParameter(RATING_PARAMETER, "OUT_OF_RANGE", NOT_INT32).getResponse());
        }
    }

    private WebApplicationException invalidParameter(String field, String title,
            String description) {
        return new WebApplicationException(new ProblemDetails("/problems/validation-failed",
                "Invalid request parameters", 400, null,
                List.of(new ProblemDetails.Issue(field, title, description))).toResponse());
    }

    private Worker readWorker(String body, boolean update) {
        RequestFields fields = requestReader.read(body);
        return fields.validate(entities.worker(fields, update), "user", "creation_date");
    }

    private Response conflict(Change change, Worker worker) {
        if (PERSON.equals(change.subject())) {
            return problem(409, "/problems/person-already-assigned", "Person already assigned",
                    "Person with id=" + change.subjectId()
                            + " is already assigned to another worker.",
                    "person_id", change.subjectId());
        }
        return Response.status(409)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(Map.of("type", "/problems/organization-name-already-exists",
                        "title", "Organization name already exists", STATUS, 409,
                        "detail", "Another organization with this full_name already exists.",
                        "full_name", worker.getOrganization().getFullName()))
                .build();
    }

    /** У {@code address-not-found} в контракте нет поля с ID, у остальных ссылок есть. */
    private Response referenceNotFound(Change change) {
        long id = change.subjectId();
        return switch (change.subject()) {
            case "address" -> new ProblemDetails(404, "/problems/address-not-found",
                    "Address not found", "Address with id=" + id + MISSING)
                    .toResponse();
            case "coordinates" -> problem(404, "/problems/coordinates-not-found",
                    "Coordinates not found", "Coordinates with id=" + id + " do not exist.",
                    "coordinates_id", id);
            case ORGANIZATION -> problem(404, "/problems/organization-not-found",
                    "Organization not found", "Organization with id=" + id + MISSING,
                    "organization_id", id);
            case PERSON -> problem(404, "/problems/person-not-found", "Person not found",
                    "Person with id=" + id + MISSING, "person_id", id);
            default -> problem(404, "/problems/location-not-found", "Location not found",
                    "Location with id=" + id + MISSING, "location_id", id);
        };
    }

    private Response notFound(int id) {
        return problem(404, "/problems/worker-not-found", "Worker not found",
                "Worker with id=" + id + MISSING, "worker_id", id);
    }

    private Response problem(int status, String type, String title, String detail,
            String idField, long id) {
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(Map.of("type", type, "title", title, STATUS, status, "detail", detail,
                        idField, id))
                .build();
    }
}
