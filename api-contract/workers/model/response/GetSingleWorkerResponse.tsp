import "../../../common/model/WorkerFields.tsp";
import "../../../organizations/model/response/GetSingleOrganizationResponse.tsp";
import "../../../coordinates/model/response/GetSingleCoordinatesResponse.tsp";
import "../../../persons/model/response/GetSinglePersonResponse.tsp";

using WorkersApi.Domain;
using WorkersApi.Organizations;
using WorkersApi.Coordinates;
using WorkersApi.Persons;

namespace WorkersApi.Workers;

const getSingleWorkerExample: GetSingleWorkerResponse = #{
    ...workerExample,
    id: 42,
    creation_date: utcDateTime.fromISO("2026-10-06T12:00:00Z"),
    coordinates: getSingleCoordinatesExample,
    person: getSinglePersonExample,
    organization: getSingleOrganizationExample
};

const getSingleWorkerWithoutOrganizationExample: GetSingleWorkerResponse = #{
    ...getSingleWorkerExample,
    organization: null
};

@doc("Данные работника.")
@example(getSingleWorkerExample)
@example(getSingleWorkerWithoutOrganizationExample)
model GetSingleWorkerResponse {
    ...OmitProperties<WorkerFields, "coordinates" | "person">;

    @doc("ID работника.")
    @minValueExclusive(0)
    id: int32;

    @doc("Дата создания работника.")
    creation_date: utcDateTime;

    @doc("Организация работника или null.")
    organization: GetSingleOrganizationResponse | null;

    @doc("Координаты работника.")
    coordinates: GetSingleCoordinatesResponse;

    @doc("Персона работника или null.")
    person: GetSinglePersonResponse | null;
}
