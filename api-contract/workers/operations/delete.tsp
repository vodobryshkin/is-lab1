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
    parameters: #{ csrf_token: WorkersApi.Security.csrfTokenExample, id: 42 },
    returnType: #{ statusCode: 204 }
};

const deleteWorkerInvalidIdOperationExample = #{
    returnType: invalidWorkerIdErrorExample
};

const deleteWorkerNotFoundOperationExample = #{
    returnType: workerNotFoundErrorExample
};

const deleteWorkerConflictOperationExample = #{
    returnType: deleteWorkerConflictExample
};

@delete
@route("/{id}")
@useAuth(WorkersApi.Security.SessionCookieAuth)
@summary("Удалить работника.")
@doc("Удаляет работника, если на него не ссылаются другие объекты.")
@opExample(deleteWorkerSuccessOperationExample)
@opExample(deleteWorkerInvalidIdOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(forbiddenOperationExample)
@opExample(deleteWorkerNotFoundOperationExample)
@opExample(deleteWorkerConflictOperationExample)
op deleteWorker(
    ...WorkersApi.Security.CsrfTokenHeader,
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