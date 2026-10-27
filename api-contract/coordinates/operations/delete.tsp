import "@typespec/http";

import "../model/error/CoordinatesErrors.tsp";
import "../../common/ok/NoContentResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Ok;
using WorkersApi.Errors;
using WorkersApi.Coordinates.Errors;

namespace WorkersApi.Coordinates;

const deleteCoordinatesSuccessOperationExample = #{
    parameters: #{ csrf_token: WorkersApi.Security.csrfTokenExample, id: 5 },
    returnType: #{ statusCode: 204 }
};

@delete
@route("/{id}")
@summary("Удалить координаты.")
@doc("Удаляет координаты, если они не используются работниками.")
@returnsDoc("Координаты удалены.")
@opExample(deleteCoordinatesSuccessOperationExample)
@opExample(invalidIdOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(forbiddenOperationExample)
@opExample(coordinatesNotFoundOperationExample)
@opExample(deleteCoordinatesConflictOperationExample)
op deleteCoordinates(
    ...WorkersApi.Security.CsrfTokenHeader,
    @path
    @minValue(1)
    id: int64
):
    WorkersApi.Ok.NoContentResponse
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | NotFoundError
    | ConflictError;
