import "@typespec/http";

import "../main.tsp";
import "../../common/model/PersonRequest.tsp";
import "../../common/model/IdResponse.tsp";
import "../../common/ok/CreatedResponse.tsp";
import "../model/error/PersonErrors.tsp";

using TypeSpec.Http;
using WorkersApi.Domain;
using WorkersApi.Ok;
using WorkersApi.Errors;
using WorkersApi.Persons.Errors;

namespace WorkersApi.Persons;

const createPersonSuccessOperationExample = #{
    parameters: #{ csrf_token: WorkersApi.Security.csrfTokenExample, body: personWriteExample },
    returnType: #{
        statusCode: 201,
        contentType: "application/json",
        location: "/api/v1/persons/42",
        body: idResponseExample
    }
};

const createPersonWithExistingLocationOperationExample = #{
    ...createPersonSuccessOperationExample,
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        body: personWithExistingLocationExample
    }
};

@post
@route("")
@summary("Создать персону.")
@doc("Создаёт персону без работника и привязывает новую или существующую локацию.")
@returnsDoc("ID созданного объекта.")
@opExample(createPersonSuccessOperationExample)
@opExample(createPersonWithExistingLocationOperationExample)
@opExample(locationNotFoundOperationExample)
@opExample(badRequestOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(forbiddenOperationExample)
@opExample(personValidationOperationExample)
op createPerson(
    ...WorkersApi.Security.CsrfTokenHeader,
    @body body: PersonWriteRequest
):
    WorkersApi.Ok.CreatedResponse<IdResponse>
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | NotFoundError<LocationNotFoundDetails>
    | PersonValidationError;
