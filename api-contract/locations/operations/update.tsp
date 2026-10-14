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
    parameters: #{ id: 7, body: locationWriteExample },
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
@opExample(missingTokenOperationExample)
@opExample(invalidTokenOperationExample)
@opExample(forbiddenOperationExample)
@opExample(locationNotFoundOperationExample)
@opExample(locationValidationOperationExample)
op updateLocation(
    @path
    @minValue(1)
    id: int32,
    @body body: LocationWriteRequest
):
    WorkersApi.Ok.NoContentResponse
    | ValidationError<400, ParameterValidationIssue>
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | NotFoundError<LocationNotFoundDetails>
    | LocationValidationError;
