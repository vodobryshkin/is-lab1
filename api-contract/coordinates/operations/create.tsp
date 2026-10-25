import "@typespec/http";

import "../main.tsp";
import "../../common/model/IdResponse.tsp";
import "../../common/ok/CreatedResponse.tsp";
import "../model/error/CoordinatesErrors.tsp";

using TypeSpec.Http;
using WorkersApi.Domain;
using WorkersApi.Ok;
using WorkersApi.Errors;
using WorkersApi.Coordinates.Errors;

namespace WorkersApi.Coordinates;

const createCoordinatesSuccessOperationExample = #{
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        body: coordinatesWriteExample
    },
    returnType: #{
        statusCode: 201,
        contentType: "application/json",
        location: "/api/v1/coordinates/42",
        body: idResponseExample
    }
};

@post
@route("")
@summary("Создать координаты.")
@doc("Создаёт координаты.")
@returnsDoc("ID созданного объекта.")
@opExample(createCoordinatesSuccessOperationExample)
@opExample(badRequestOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(forbiddenOperationExample)
@opExample(coordinatesValidationOperationExample)
op createCoordinates(
    ...WorkersApi.Security.CsrfTokenHeader,
    @body body: CoordinatesWriteRequest
):
    WorkersApi.Ok.CreatedResponse<IdResponse>
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | CoordinatesValidationError;
