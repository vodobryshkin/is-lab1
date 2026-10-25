import "@typespec/http";

import "../model/error/OrganizationErrors.tsp";
import "../../common/ok/NoContentResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Ok;
using WorkersApi.Errors;
using WorkersApi.Organizations.Errors;

namespace WorkersApi.Organizations;

const deleteOrganizationSuccessOperationExample = #{
    parameters: #{ csrf_token: WorkersApi.Security.csrfTokenExample, id: 42 },
    returnType: #{ statusCode: 204 }
};

@delete
@route("/{id}")
@summary("Удалить организацию.")
@doc("Удаляет организацию, если на неё не ссылаются работники.")
@returnsDoc("Организация удалена.")
@opExample(deleteOrganizationSuccessOperationExample)
@opExample(organizationInvalidIdOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(forbiddenOperationExample)
@opExample(organizationNotFoundOperationExample)
@opExample(deleteOrganizationConflictOperationExample)
op deleteOrganization(
    ...WorkersApi.Security.CsrfTokenHeader,
    @path
    @minValue(1)
    id: int64
):
    WorkersApi.Ok.NoContentResponse
    | ValidationError<400, ParameterValidationIssue>
    | UnauthorizedError
    | ForbiddenError
    | OrganizationNotFoundError
    | DeleteOrganizationConflictError;