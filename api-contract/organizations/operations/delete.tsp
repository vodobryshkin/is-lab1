import "@typespec/http";

import "../model/error/OrganizationErrors.tsp";
import "../../common/ok/NoContentResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Ok;
using WorkersApi.Errors;
using WorkersApi.Organizations.Errors;

namespace WorkersApi.Organizations;

const deleteOrganizationSuccessOperationExample = #{
    parameters: #{ id: 42 },
    returnType: #{ statusCode: 204 }
};

@delete
@route("/{id}")
@summary("Удалить организацию.")
@doc("Удаляет организацию, если на неё не ссылаются работники.")
@returnsDoc("Организация удалена.")
@opExample(deleteOrganizationSuccessOperationExample)
@opExample(organizationInvalidIdOperationExample)
@opExample(organizationMissingTokenOperationExample)
@opExample(organizationInvalidTokenOperationExample)
@opExample(organizationForbiddenOperationExample)
@opExample(organizationNotFoundOperationExample)
@opExample(deleteOrganizationConflictOperationExample)
op deleteOrganization(
    @path
    @minValue(1)
    id: int32
):
    WorkersApi.Ok.NoContentResponse
    | ValidationError<400, ParameterValidationIssue>
    | UnauthorizedError
    | ForbiddenError
    | OrganizationNotFoundError
    | DeleteOrganizationConflictError;