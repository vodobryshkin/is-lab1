import "../../../common/error/ProblemResponse.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Workers.Errors;

const deleteWorkerConflictExample: DeleteWorkerConflictError = #{
    statusCode: 409,
    contentType: "application/json",
    body: #{
        type: "/problems/worker-has-linked-objects",
        title: "Worker has linked objects",
        status: 409,
        detail: "Worker with id=42 is referenced by a person and cannot be deleted.",
        worker_id: 42
    }
};

@error
@doc("Удалению мешают связанные объекты.")
@example(deleteWorkerConflictExample)
model DeleteWorkerConflictError is ProblemResponse<
    409,
    {
        ...OmitProperties<ProblemDetails<409>, "type">;

        type: "/problems/worker-has-linked-objects";

        @doc("ID работника.")
        @minValueExclusive(0)
        worker_id: int32;
    }
>;