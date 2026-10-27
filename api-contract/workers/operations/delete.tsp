import "@typespec/http";


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
    returnType: invalidIdExample
};

const deleteWorkerNotFoundOperationExample = #{
    returnType: workerNotFoundExample
};

const deleteWorkerConflictOperationExample = #{
    returnType: #{
        statusCode: 409,
        contentType: "application/json",
        body: #{
            message: "Worker with id=42 is referenced by a person and cannot be deleted."
        }
    }
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
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | NotFoundError
    | ConflictError;