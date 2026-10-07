import "../../../common/error/ProblemResponse.tsp";
import "./WorkerNotFoundError.tsp";
import "./CreateWorkerOrganizationNotFoundError.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Workers.Errors;

@error
@doc("Работник или организация не найдены.")
@example(workerNotFoundErrorExample)
@example(createWorkerOrganizationNotFoundErrorExample)
model UpdateWorkerNotFoundError is ProblemResponse<
    404,
    {
        ...OmitProperties<ProblemDetails<404>, "type">;
        type: "/problems/worker-not-found";

        @doc("ID работника.")
        @minValueExclusive(0)
        worker_id: int32;
    } | {
        ...OmitProperties<ProblemDetails<404>, "type">;
        type: "/problems/organization-not-found";

        @doc("ID организации.")
        @minValueExclusive(0)
        organization_id: int32;
    }
>;