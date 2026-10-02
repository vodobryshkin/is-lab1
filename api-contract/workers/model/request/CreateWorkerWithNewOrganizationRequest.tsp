import "../../../domain/model/WorkerFields.tsp";
import "../../../domain/model/OrganizationFields.tsp";

namespace WorkersApi.Workers;

using WorkersApi.Domain;

@doc("Создание работника вместе с организацией, в которой он работает.")
@example(#{
    ...workerExample,
    organization: organizationExample
})
model CreateWorkerWithNewOrganizationRequest {
    ...WorkerFields;

    @doc("Данные об организации, в которой работает работник.")
    organization: OrganizationFields;
}