import "@typespec/http";

import "../model/response/GetSingleLocationResponse.tsp";
import "../model/error/LocationErrors.tsp";
import "../../common/ok/OkResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Ok;
using WorkersApi.Errors;

namespace WorkersApi.Locations;

const getLocationSuccessOperationExample = #{
    parameters: #{ id: 7 },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: getSingleLocationExample
    }
};

@get
@route("/{id}")
@summary("Получить локацию.")
@doc("Возвращает данные локации.")
@returnsDoc("Данные локации.")
@opExample(getLocationSuccessOperationExample)
@opExample(invalidIdOperationExample)
@opExample(missingTokenOperationExample)
@opExample(invalidTokenOperationExample)
@opExample(locationNotFoundOperationExample)
op getLocation(
    @path
    @minValue(1)
    id: int32
):
    WorkersApi.Ok.OkResponse<GetSingleLocationResponse>
    | ValidationError<400, ParameterValidationIssue>
    | UnauthorizedError
    | NotFoundError<LocationNotFoundDetails>;
