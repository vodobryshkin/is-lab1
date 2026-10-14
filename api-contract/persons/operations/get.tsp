import "@typespec/http";

import "../model/response/GetSinglePersonResponse.tsp";
import "../model/error/PersonErrors.tsp";
import "../../common/ok/OkResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Ok;
using WorkersApi.Errors;

namespace WorkersApi.Persons;

const getPersonSuccessOperationExample = #{
    parameters: #{ id: 10 },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: getSinglePersonExample
    }
};

@get
@route("/{id}")
@summary("Получить персону.")
@doc("Возвращает персону и её локацию.")
@returnsDoc("Данные персоны.")
@opExample(getPersonSuccessOperationExample)
@opExample(invalidIdOperationExample)
@opExample(missingTokenOperationExample)
@opExample(invalidTokenOperationExample)
@opExample(personNotFoundOperationExample)
op getPerson(
    @path
    @minValue(1)
    id: int32
):
    WorkersApi.Ok.OkResponse<GetSinglePersonResponse>
    | ValidationError<400, ParameterValidationIssue>
    | UnauthorizedError
    | NotFoundError<PersonNotFoundDetails>;
