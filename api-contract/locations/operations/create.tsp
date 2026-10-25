import "@typespec/http";

import "../main.tsp";
import "../../common/model/IdResponse.tsp";
import "../../common/ok/CreatedResponse.tsp";
import "../model/error/LocationErrors.tsp";

using TypeSpec.Http;
using WorkersApi.Domain;
using WorkersApi.Ok;
using WorkersApi.Errors;
using WorkersApi.Locations.Errors;

namespace WorkersApi.Locations;

const createLocationSuccessOperationExample = #{
    parameters: #{ csrf_token: WorkersApi.Security.csrfTokenExample, body: locationWriteExample },
    returnType: #{
        statusCode: 201,
        contentType: "application/json",
        location: "/api/v1/locations/42",
        body: idResponseExample
    }
};

@post
@route("")
@summary("Создать локацию.")
@doc("Создаёт локацию.")
@returnsDoc("ID созданного объекта.")
@opExample(createLocationSuccessOperationExample)
@opExample(badRequestOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(forbiddenOperationExample)
@opExample(locationValidationOperationExample)
op createLocation(
    ...WorkersApi.Security.CsrfTokenHeader,
    @body body: LocationWriteRequest
):
    WorkersApi.Ok.CreatedResponse<IdResponse>
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | LocationValidationError;
