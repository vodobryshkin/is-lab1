import "../../../common/error/ProblemResponse.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Workers.Errors;

const workerNotFoundErrorExample: WorkerNotFoundError = #{
    statusCode: 404,
    contentType: "application/json",
    body: #{
        type: "/problems/worker-not-found",
        title: "Worker not found",
        status: 404,
        detail: "Worker with id=999 does not exist.",
        worker_id: 999
    }
};

@error
@doc("Работник не найден.")
@example(workerNotFoundErrorExample)
model WorkerNotFoundError is ProblemResponse<
    404,
    {
        ...OmitProperties<ProblemDetails<404>, "type">;

        type: "/problems/worker-not-found";

        @doc("ID работника.")
        @minValueExclusive(0)
        worker_id: int32;
    }
>;