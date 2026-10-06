import "@typespec/http";

import "../model/response/GetSingleWorkerResponse.tsp";
import "../model/error/InvalidWorkerIdError.tsp";
import "../model/error/WorkerNotFoundError.tsp";

import "../../common/error/UnauthorizedError.tsp";
import "../../common/ok/OkResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Errors;
using WorkersApi.Workers.Errors;

namespace WorkersApi.Workers;

const getWorkerSuccessOperationExample = #{
    parameters: #{ id: 42 },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: getSingleWorkerExample
    }
};

const getWorkerWithoutOrganizationOperationExample = #{
    parameters: #{ id: 42 },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: getSingleWorkerWithoutOrganizationExample
    }
};

const getWorkerMissingTokenExample: UnauthorizedError = #{
    statusCode: 401,
    wwwAuthenticate: "Bearer realm=\"Workers API\"",
    contentType: "application/problem+json",
    body: #{
        type: "/problems/missing-token",
        title: "Authentication required",
        status: 401,
        detail: "A bearer token is required."
    }
};

const getWorkerInvalidTokenExample: UnauthorizedError = #{
    statusCode: 401,
    wwwAuthenticate: "Bearer realm=\"Workers API\", error=\"invalid_token\"",
    contentType: "application/problem+json",
    body: #{
        type: "/problems/invalid-token",
        title: "Invalid token",
        status: 401,
        detail: "The bearer token is invalid or expired."
    }
};

const getWorkerInvalidIdOperationExample = #{
    returnType: invalidWorkerIdErrorExample
};

const getWorkerMissingTokenOperationExample = #{
    returnType: getWorkerMissingTokenExample
};

const getWorkerInvalidTokenOperationExample = #{
    returnType: getWorkerInvalidTokenExample
};

const getWorkerNotFoundOperationExample = #{
    parameters: #{ id: 999 },
    returnType: workerNotFoundErrorExample
};

@get
@route("/{id}")
@useAuth(BearerAuth)
@summary("Получить работника.")
@doc("Возвращает работника и его вложенные объекты.")
@opExample(getWorkerSuccessOperationExample)
@opExample(getWorkerWithoutOrganizationOperationExample)
@opExample(getWorkerInvalidIdOperationExample)
@opExample(getWorkerMissingTokenOperationExample)
@opExample(getWorkerInvalidTokenOperationExample)
@opExample(getWorkerNotFoundOperationExample)
op getWorker(
    @path
    @minValueExclusive(0)
    id: int32
):
    WorkersApi.Ok.OkResponse<GetSingleWorkerResponse>
    | InvalidWorkerIdError
    | UnauthorizedError
    | WorkerNotFoundError;