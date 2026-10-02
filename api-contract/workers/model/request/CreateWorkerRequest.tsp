import "@typespec/openapi3";

import "./CreateWorkerWithExistingOrganizationRequest.tsp";
import "./CreateWorkerWithNewOrganizationRequest.tsp";
import "./CreateWorkerWithoutOrganizationRequest.tsp";

namespace WorkersApi.Workers;

using TypeSpec.OpenAPI;

@doc("Данные для создания работника.")
@oneOf
union CreateWorkerRequest {
    @doc("Используется, когда мы хотим добавить работника в уже существующую организацию, передав id этой организации.")
    existingOrganization: CreateWorkerWithExistingOrganizationRequest,

    @doc("Используется, когда мы хотим добавить работника в организацию, которой ещё не существует, создав ее вместе с работником.")
    newOrganization: CreateWorkerWithNewOrganizationRequest,

    @doc("Используется, когда мы хотим добавить работника без привязки к организации.")
    withoutOrganization: CreateWorkerWithoutOrganizationRequest,
}