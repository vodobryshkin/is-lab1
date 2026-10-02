import "@typespec/openapi3";

import "./CreateWorkerWithExistingOrganizationRequest.tsp";
import "./CreateWorkerWithNewOrganizationRequest.tsp";
import "./CreateWorkerWithoutOrganizationRequest.tsp";

namespace WorkersApi.Workers;

@doc("Данные для создания работника.")
@TypeSpec.OpenAPI.oneOf
union CreateWorkerRequest {
    existingOrganization: CreateWorkerWithExistingOrganizationRequest,
    newOrganization: CreateWorkerWithNewOrganizationRequest,
    withoutOrganization: CreateWorkerWithoutOrganizationRequest,
}