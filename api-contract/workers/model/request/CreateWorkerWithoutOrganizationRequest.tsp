import "../../../domain/model/WorkerFields.tsp";
import "../../../domain/model/OrganizationFields.tsp";

namespace WorkersApi.Workers;

using WorkersApi.Domain;

@doc("Создание работника без организации.")
@example(#{...workerExample})
model CreateWorkerWithoutOrganizationRequest {
    ...WorkerFields;
}