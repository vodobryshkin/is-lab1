import "../../../domain/model/WorkerFields.tsp";
import "../../../domain/model/OrganizationFields.tsp";

namespace WorkersApi.Workers;

using WorkersApi.Domain;

@doc("Создание работника вместе с существующей организацией.")
@example(#{
    ...workerExample,
    organization_id: 1
})
model CreateWorkerWithExistingOrganizationRequest {
    ...WorkerFields;

    @doc("Id организации, к которой нужно прикрепить работника.")
    @minValueExclusive(0)
    organization_id: int32;
}