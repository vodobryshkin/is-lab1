import "@typespec/http";

import "../../common/error/NotFoundError.tsp";
import "../../common/error/ConflictError.tsp";

import "../model/request/UpdateWorkerRequest.tsp";
import "../model/error/InvalidWorkerIdError.tsp";
import "../model/error/UpdateWorkerNotFoundError.tsp";
import "../model/error/UpdateWorkerValidationError.tsp";

import "../../common/error/BadRequestError.tsp";
import "../../common/error/UnauthorizedError.tsp";
import "../../common/error/ForbiddenError.tsp";
import "../../common/ok/NoContentResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Domain;
using WorkersApi.Errors;
using WorkersApi.Workers.Errors;

namespace WorkersApi.Workers;

const updateWorkerSuccessOperationExample = #{
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        id: 42,
        body: updateWorkerExample
    },
    returnType: #{ statusCode: 204 }
};

const updateWorkerWithoutOrganizationOperationExample = #{
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        id: 42,
        body: updateWorkerWithoutOrganizationExample
    },
    returnType: #{ statusCode: 204 }
};

const updateWorkerInvalidIdOperationExample = #{
    returnType: invalidWorkerIdErrorExample
};

const updateWorkerBadRequestOperationExample = #{
    returnType: #{
        ...badRequestExample,
        body: #{
            type: "/problems/malformed-json",
            status: 400,
            title: "Malformed JSON",
            detail: "The request body contains malformed JSON."
        }
    }
};

const updateWorkerNotFoundOperationExample = #{
    returnType: workerNotFoundErrorExample
};

const updateWorkerOrganizationNotFoundOperationExample = #{
    returnType: createWorkerOrganizationNotFoundErrorExample
};

const updateWorkerValidationOperationExample = #{
    returnType: updateWorkerValidationExample
};

const updateWorkerWithExistingObjectsOperationExample = #{
    ...updateWorkerSuccessOperationExample,
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        id: 42, body: #{
            ...updateWorkerExample,
            coordinates: existingCoordinatesExample,
            person: existingPersonExample
        }
    }
};

const updateWorkerWithExistingPersonLocationOperationExample = #{
    ...updateWorkerSuccessOperationExample,
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        id: 42, body: #{
            ...updateWorkerExample,
            person: personWithExistingLocationExample
        }
    }
};

@put
@route("/{id}")
@useAuth(WorkersApi.Security.SessionCookieAuth)
@summary("Обновить работника.")
@doc("Заменяет поля и привязки в одной транзакции. person: null отвязывает персону без удаления; organization_id: null отвязывает организацию.")
@opExample(locationNotFoundOperationExample)
@opExample(coordinatesNotFoundOperationExample)
@opExample(personNotFoundOperationExample)
@opExample(personAlreadyAssignedOperationExample)
@opExample(updateWorkerWithExistingObjectsOperationExample)
@opExample(updateWorkerWithExistingPersonLocationOperationExample)
@opExample(updateWorkerReferenceValidationOperationExample)
@opExample(updateWorkerSuccessOperationExample)
@opExample(updateWorkerWithoutOrganizationOperationExample)
@opExample(updateWorkerInvalidIdOperationExample)
@opExample(updateWorkerBadRequestOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(forbiddenOperationExample)
@opExample(updateWorkerNotFoundOperationExample)
@opExample(updateWorkerOrganizationNotFoundOperationExample)
@opExample(updateWorkerValidationOperationExample)
op updateWorker(
    ...WorkersApi.Security.CsrfTokenHeader,
    @path
    @minValue(1)
    id: int32,
    @body body: UpdateWorkerRequest
):
    WorkersApi.Ok.NoContentResponse
    | InvalidWorkerIdError
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | UpdateWorkerNotFoundError
    | ConflictError<PersonAlreadyAssignedDetails>
    | UpdateWorkerValidationError;
