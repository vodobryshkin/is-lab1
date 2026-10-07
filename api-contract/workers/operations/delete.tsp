import "@typespec/http";

import "../model/error/InvalidWorkerIdError.tsp";
import "../model/error/WorkerNotFoundError.tsp";
import "../model/error/DeleteWorkerConflictError.tsp";

import "../../common/error/UnauthorizedError.tsp";
import "../../common/error/ForbiddenError.tsp";
import "../../common/ok/NoContentResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Errors;
using WorkersApi.Workers.Errors;

namespace WorkersApi.Workers;

const deleteWorkerSuccessOperationExample = #{
    parameters: #{ id: 42 },
    returnType: #{ statusCode: 204 }
};

const deleteWorkerInvalidIdOperationExample = #{
    returnType: invalidWorkerIdErrorExample
};

const deleteWorkerUnauthorizedOperationExample = #{
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

const deleteWorkerForbiddenOperationExample = #{
    returnType: #{
        ...forbiddenExample,
        body: #{
            type: "/problems/insufficient-permissions",
            status: 403,
            title: "Insufficient permissions",
            detail: "Deleting this worker is not allowed."
        }
    }
};

const deleteWorkerNotFoundOperationExample = #{
    returnType: workerNotFoundErrorExample
};

const deleteWorkerConflictOperationExample = #{
    returnType: deleteWorkerConflictExample
};

@delete
@route("/{id}")
@useAuth(BearerAuth)
@summary("Удалить работника.")
@doc("Удаляет работника, если на него не ссылаются другие объекты.")
@opExample(deleteWorkerSuccessOperationExample)
@opExample(deleteWorkerInvalidIdOperationExample)
@opExample(deleteWorkerUnauthorizedOperationExample)
@opExample(deleteWorkerForbiddenOperationExample)
@opExample(deleteWorkerNotFoundOperationExample)
@opExample(deleteWorkerConflictOperationExample)
op deleteWorker(
    @path
    @minValue(1)
    id: int32
):
    WorkersApi.Ok.NoContentResponse
    | InvalidWorkerIdError
    | UnauthorizedError
    | ForbiddenError
    | WorkerNotFoundError
    | DeleteWorkerConflictError;