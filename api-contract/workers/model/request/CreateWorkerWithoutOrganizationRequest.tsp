import "../../../common/model/WorkerFields.tsp";
import "../../../common/model/OrganizationFields.tsp";

namespace WorkersApi.Workers;

using WorkersApi.Domain;

@doc("Создание работника без организации.")
@example(#{...workerExample})
model CreateWorkerWithoutOrganizationRequest {
    ...WorkerFields;
}