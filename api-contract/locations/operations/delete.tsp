import "@typespec/http";

import "../model/error/LocationErrors.tsp";
import "../../common/ok/NoContentResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Ok;
using WorkersApi.Errors;
using WorkersApi.Locations.Errors;

namespace WorkersApi.Locations;

const deleteLocationSuccessOperationExample = #{
    parameters: #{ csrf_token: WorkersApi.Security.csrfTokenExample, id: 7 },
    returnType: #{ statusCode: 204 }
};

@delete
@route("/{id}")
@summary("Удалить локацию.")
@doc("Удаляет локацию, если она не используется адресами или персонами.")
@returnsDoc("Локация удалена.")
@opExample(deleteLocationSuccessOperationExample)
@opExample(invalidIdOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(forbiddenOperationExample)
@opExample(locationNotFoundOperationExample)
@opExample(deleteLocationConflictOperationExample)
op deleteLocation(
    ...WorkersApi.Security.CsrfTokenHeader,
    @path
    @minValue(1)
    id: int64
):
    WorkersApi.Ok.NoContentResponse
    | ValidationError<400, ParameterValidationIssue>
    | UnauthorizedError
    | ForbiddenError
    | NotFoundError<LocationNotFoundDetails>
    | DeleteLocationConflictError;
