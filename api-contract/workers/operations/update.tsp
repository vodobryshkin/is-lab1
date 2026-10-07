import "@typespec/http";

import "../model/request/UpdateWorkerRequest.tsp";
import "../model/error/InvalidWorkerIdError.tsp";
import "../model/error/UpdateWorkerNotFoundError.tsp";
import "../model/error/UpdateWorkerValidationError.tsp";

import "../../common/error/BadRequestError.tsp";
import "../../common/error/UnauthorizedError.tsp";
import "../../common/error/ForbiddenError.tsp";
import "../../common/ok/NoContentResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Errors;
using WorkersApi.Workers.Errors;

namespace WorkersApi.Workers;

const updateWorkerSuccessOperationExample = #{
    parameters: #{ id: 42, body: updateWorkerExample },
    returnType: #{ statusCode: 204 }
};

const updateWorkerWithoutOrganizationOperationExample = #{
    parameters: #{ id: 42, body: updateWorkerWithoutOrganizationExample },
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

const updateWorkerUnauthorizedOperationExample = #{
    returnType: #{
        ...unauthorizedExample,
        body: #{
            type: "/problems/invalid-token",
            status: 401,
            title: "Invalid token",
            detail: "The bearer token is invalid or expired."
        }
    }
};

const updateWorkerForbiddenOperationExample = #{
    returnType: #{
        ...forbiddenExample,
        body: #{
            type: "/problems/insufficient-permissions",
            status: 403,
            title: "Insufficient permissions",
            detail: "Updating this worker is not allowed."
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

@put
@route("/{id}")
@useAuth(BearerAuth)
@summary("Обновить работника.")
@doc("Заменяет изменяемые поля работника и его вложенных объектов.")
@opExample(updateWorkerSuccessOperationExample)
@opExample(updateWorkerWithoutOrganizationOperationExample)
@opExample(updateWorkerInvalidIdOperationExample)
@opExample(updateWorkerBadRequestOperationExample)
@opExample(updateWorkerUnauthorizedOperationExample)
@opExample(updateWorkerForbiddenOperationExample)
@opExample(updateWorkerNotFoundOperationExample)
@opExample(updateWorkerOrganizationNotFoundOperationExample)
@opExample(updateWorkerValidationOperationExample)
op updateWorker(
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
    | UpdateWorkerValidationError;