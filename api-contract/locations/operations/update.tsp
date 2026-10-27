import "@typespec/http";

import "../main.tsp";
import "../../common/ok/NoContentResponse.tsp";
import "../model/error/LocationErrors.tsp";

using TypeSpec.Http;
using WorkersApi.Domain;
using WorkersApi.Ok;
using WorkersApi.Errors;
using WorkersApi.Locations.Errors;

namespace WorkersApi.Locations;

const updateLocationSuccessOperationExample = #{
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        id: 7,
        body: locationWriteExample
    },
    returnType: #{ statusCode: 204 }
};

@put
@route("/{id}")
@summary("Обновить локацию.")
@doc("Заменяет все поля локации, включая используемые адресами и персонами.")
@returnsDoc("Локация обновлена.")
@opExample(updateLocationSuccessOperationExample)
@opExample(invalidIdOperationExample)
@opExample(badRequestOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(forbiddenOperationExample)
@opExample(locationNotFoundOperationExample)
@opExample(locationValidationOperationExample)
op updateLocation(
    ...WorkersApi.Security.CsrfTokenHeader,
    @path
    @minValue(1)
    id: int64,
    @body body: LocationWriteRequest
):
    WorkersApi.Ok.NoContentResponse
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | NotFoundError
    | LocationValidationError;
