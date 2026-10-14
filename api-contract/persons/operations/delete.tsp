import "@typespec/http";

import "../model/error/PersonErrors.tsp";
import "../../common/ok/NoContentResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Ok;
using WorkersApi.Errors;
using WorkersApi.Persons.Errors;

namespace WorkersApi.Persons;

const deletePersonSuccessOperationExample = #{
    parameters: #{ id: 10 },
    returnType: #{ statusCode: 204 }
};

@delete
@route("/{id}")
@summary("Удалить персону.")
@doc("Удаляет персону, если она не связана с работником. Локацию не удаляет.")
@returnsDoc("Персона удалена.")
@opExample(deletePersonSuccessOperationExample)
@opExample(invalidIdOperationExample)
@opExample(missingTokenOperationExample)
@opExample(invalidTokenOperationExample)
@opExample(forbiddenOperationExample)
@opExample(personNotFoundOperationExample)
@opExample(deletePersonConflictOperationExample)
op deletePerson(
    @path
    @minValue(1)
    id: int64
):
    WorkersApi.Ok.NoContentResponse
    | ValidationError<400, ParameterValidationIssue>
    | UnauthorizedError
    | ForbiddenError
    | NotFoundError<PersonNotFoundDetails>
    | DeletePersonConflictError;
