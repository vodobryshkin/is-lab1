import "@typespec/http";

import "../model/response/GetSingleOrganizationResponse.tsp";
import "../model/error/OrganizationErrors.tsp";
import "../../common/ok/OkResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Ok;
using WorkersApi.Errors;
using WorkersApi.Organizations.Errors;

namespace WorkersApi.Organizations;

const getOrganizationSuccessOperationExample = #{
    parameters: #{ id: 42 },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: getSingleOrganizationExample
    }
};

@get
@route("/{id}")
@summary("Получить организацию.")
@doc("Возвращает организацию и её вложенные адреса.")
@returnsDoc("Данные организации.")
@opExample(getOrganizationSuccessOperationExample)
@opExample(organizationInvalidIdOperationExample)
@opExample(organizationMissingTokenOperationExample)
@opExample(organizationInvalidTokenOperationExample)
@opExample(organizationNotFoundOperationExample)
op getOrganization(
    @path
    @minValue(1)
    id: int32
):
    WorkersApi.Ok.OkResponse<GetSingleOrganizationResponse>
    | ValidationError<400, ParameterValidationIssue>
    | UnauthorizedError
    | OrganizationNotFoundError;