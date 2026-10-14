import "@typespec/openapi3";

import "./CreateWorkerWithExistingOrganizationRequest.tsp";
import "./CreateWorkerWithNewOrganizationRequest.tsp";
import "./CreateWorkerWithoutOrganizationRequest.tsp";

namespace WorkersApi.Workers;

using TypeSpec.OpenAPI;

@doc("Данные для создания работника.")
@oneOf
union CreateWorkerRequest {
    @doc("Работник в существующей организации.")
    existing_organization: CreateWorkerWithExistingOrganizationRequest,

    @doc("Работник с новой организацией.")
    new_organization: CreateWorkerWithNewOrganizationRequest,

    @doc("Работник без организации.")
    without_organization: CreateWorkerWithoutOrganizationRequest,
}