import "../../../common/model/WorkerFields.tsp";

using WorkersApi.Domain;

namespace WorkersApi.Workers;

const createWorkerWithoutOrganizationExample:
    CreateWorkerWithoutOrganizationRequest = #{
        ...workerExample
    };

@doc("Создание работника без организации.")
@example(createWorkerWithoutOrganizationExample)
model CreateWorkerWithoutOrganizationRequest {
    ...WorkerFields;
    ...Record<never>;
}