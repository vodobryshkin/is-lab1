import "../../../common/model/WorkerFields.tsp";

using WorkersApi.Domain;

namespace WorkersApi.Workers;

const createWorkerWithExistingOrganizationExample:
    CreateWorkerWithExistingOrganizationRequest = #{
        ...workerExample,
        organization_id: 1
    };

@doc("Создание работника в существующей организации.")
@example(createWorkerWithExistingOrganizationExample)
model CreateWorkerWithExistingOrganizationRequest {
    ...WorkerFields;
    ...Record<never>;

    @doc("ID существующей организации.")
    @minValueExclusive(0)
    organization_id: int32;
}