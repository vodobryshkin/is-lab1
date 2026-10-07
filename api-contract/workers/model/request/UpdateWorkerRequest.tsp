import "../../../common/model/WorkerFields.tsp";

using WorkersApi.Domain;

namespace WorkersApi.Workers;

const updateWorkerExample: UpdateWorkerRequest = #{
    ...workerExample,
    organization_id: 1
};

const updateWorkerWithoutOrganizationExample: UpdateWorkerRequest = #{
    ...updateWorkerExample,
    organization_id: null
};

@doc("Данные для обновления работника.")
@example(updateWorkerExample)
@example(updateWorkerWithoutOrganizationExample)
model UpdateWorkerRequest {
    ...WorkerFields;
    ...Record<never>;

    @doc("ID существующей организации или null.")
    @minValueExclusive(0)
    organization_id: int32 | null;
}