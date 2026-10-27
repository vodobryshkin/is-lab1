import "@typespec/http";

import "../main.tsp";
import "../../common/model/PersonRequest.tsp";
import "../../common/ok/NoContentResponse.tsp";
import "../model/error/PersonErrors.tsp";

using TypeSpec.Http;
using WorkersApi.Domain;
using WorkersApi.Ok;
using WorkersApi.Errors;
using WorkersApi.Persons.Errors;

namespace WorkersApi.Persons;

const updatePersonSuccessOperationExample = #{
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        id: 10,
        body: personWriteExample
    },
    returnType: #{ statusCode: 204 }
};

const updatePersonWithExistingLocationOperationExample = #{
    ...updatePersonSuccessOperationExample,
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        id: 10,
        body: personWithExistingLocationExample
    }
};

@put
@route("/{id}")
@summary("Обновить персону.")
@doc("Заменяет поля и привязку локации в одной транзакции. Связь с работником сохраняет.")
@returnsDoc("Персона обновлена.")
@opExample(updatePersonSuccessOperationExample)
@opExample(updatePersonWithExistingLocationOperationExample)
@opExample(locationNotFoundOperationExample)
@opExample(invalidIdOperationExample)
@opExample(badRequestOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(forbiddenOperationExample)
@opExample(personNotFoundOperationExample)
@opExample(personValidationOperationExample)
op updatePerson(
    ...WorkersApi.Security.CsrfTokenHeader,
    @path
    @minValue(1)
    id: int64,
    @body body: PersonWriteRequest
):
    WorkersApi.Ok.NoContentResponse
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | NotFoundError
    | PersonValidationError;
