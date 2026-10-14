import "@typespec/http";

import "../model/response/GetSingleCoordinatesResponse.tsp";
import "../model/error/CoordinatesErrors.tsp";
import "../../common/ok/OkResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Ok;
using WorkersApi.Errors;

namespace WorkersApi.Coordinates;

const getCoordinatesSuccessOperationExample = #{
    parameters: #{ id: 5 },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: getSingleCoordinatesExample
    }
};

@get
@route("/{id}")
@summary("Получить координаты.")
@doc("Возвращает данные координат.")
@returnsDoc("Данные координат.")
@opExample(getCoordinatesSuccessOperationExample)
@opExample(invalidIdOperationExample)
@opExample(missingTokenOperationExample)
@opExample(invalidTokenOperationExample)
@opExample(coordinatesNotFoundOperationExample)
op getCoordinates(
    @path
    @minValue(1)
    id: int32
):
    WorkersApi.Ok.OkResponse<GetSingleCoordinatesResponse>
    | ValidationError<400, ParameterValidationIssue>
    | UnauthorizedError
    | NotFoundError<CoordinatesNotFoundDetails>;
