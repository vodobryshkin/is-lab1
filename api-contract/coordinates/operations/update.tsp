import "@typespec/http";

import "../main.tsp";
import "../../common/ok/NoContentResponse.tsp";
import "../model/error/CoordinatesErrors.tsp";

using TypeSpec.Http;
using WorkersApi.Domain;
using WorkersApi.Ok;
using WorkersApi.Errors;
using WorkersApi.Coordinates.Errors;

namespace WorkersApi.Coordinates;

const updateCoordinatesSuccessOperationExample = #{
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        id: 5,
        body: coordinatesWriteExample
    },
    returnType: #{ statusCode: 204 }
};

@put
@route("/{id}")
@summary("Обновить координаты.")
@doc("Заменяет все поля координат, включая используемые работниками.")
@returnsDoc("Координаты обновлены.")
@opExample(updateCoordinatesSuccessOperationExample)
@opExample(invalidIdOperationExample)
@opExample(badRequestOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(forbiddenOperationExample)
@opExample(coordinatesNotFoundOperationExample)
@opExample(coordinatesValidationOperationExample)
op updateCoordinates(
    ...WorkersApi.Security.CsrfTokenHeader,
    @path
    @minValue(1)
    id: int64,
    @body body: CoordinatesWriteRequest
):
    WorkersApi.Ok.NoContentResponse
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | NotFoundError
    | CoordinatesValidationError;
