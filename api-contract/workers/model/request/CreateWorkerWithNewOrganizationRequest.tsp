import "../../../common/model/WorkerFields.tsp";
import "../../../common/model/OrganizationFields.tsp";

using WorkersApi.Domain;

namespace WorkersApi.Workers;

const createWorkerWithNewOrganizationExample:
    CreateWorkerWithNewOrganizationRequest = #{
        ...workerExample,
        organization: organizationExample
    };

@doc("Создание работника вместе с новой организацией.")
@example(createWorkerWithNewOrganizationExample)
model CreateWorkerWithNewOrganizationRequest {
    ...WorkerFields;
    ...Record<never>;

    @doc("Данные новой организации.")
    organization: OrganizationFields;
}