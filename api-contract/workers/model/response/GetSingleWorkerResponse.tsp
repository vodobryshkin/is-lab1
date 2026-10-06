import "../../../common/model/WorkerFields.tsp";
import "../../../common/model/OrganizationFields.tsp";

using WorkersApi.Domain;

namespace WorkersApi.Workers;

const getSingleWorkerExample: GetSingleWorkerResponse = #{
    ...workerExample,
    id: 42,
    creation_date: utcDateTime.fromISO("2026-10-06T12:00:00Z"),
    organization: organizationExample
};

const getSingleWorkerWithoutOrganizationExample: GetSingleWorkerResponse = #{
    ...getSingleWorkerExample,
    organization: null
};

@doc("Данные работника.")
@example(getSingleWorkerExample)
@example(getSingleWorkerWithoutOrganizationExample)
model GetSingleWorkerResponse {
    ...WorkerFields;

    @doc("ID работника.")
    @minValueExclusive(0)
    id: int32;

    @doc("Дата создания работника.")
    creation_date: utcDateTime;

    @doc("Организация работника или null.")
    organization: OrganizationFields | null;
}