import "../../../domain/model/WorkerFields.tsp";

namespace WorkersApi.Workers;

using WorkersApi.Domain;

@withOptionalProperties
model UpdateWorkerRequest {
    ...WorkerFields;

    @minValueExclusive(0)
    organization_id: int32;

    @minValueExclusive(0)
    person_id: int32;
}