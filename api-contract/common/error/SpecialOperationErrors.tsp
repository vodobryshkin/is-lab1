import "./ValidationError.tsp";
import "../../workers/model/error/WorkerNotFoundError.tsp";
import "../../organizations/model/error/OrganizationErrors.tsp";

using WorkersApi.Workers.Errors;
using WorkersApi.Organizations.Errors;

namespace WorkersApi.Errors;

@doc("Ошибки данных специальной операции.")
union SpecialRequestValidationIssue {
    parameters: ParameterValidationIssue,

    body: {
        field: "$";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE";
        description: string;
    },

    unknown_field: {
        field: string;
        title: "UNKNOWN_FIELD";
        description: string;
    },
}

@error
@doc("Работник или организация не найдены.")
@example(workerNotFoundErrorExample)
@example(organizationNotFoundErrorExample)
model HireWorkerNotFoundError is ProblemResponse<
    404,
    {
        ...OmitProperties<ProblemDetails<404>, "type">;

        type: "/problems/worker-not-found";

        @doc("ID работника.")
        @minValue(1)
        worker_id: int32;
    }
    | {
        ...OmitProperties<ProblemDetails<404>, "type">;

        type: "/problems/organization-not-found";

        @doc("ID организации.")
        @minValue(1)
        organization_id: int32;
    }
>;

const salaryIndexationConflictErrorExample: SalaryIndexationConflictError = #{
    statusCode: 409,
    contentType: "application/json",
    body: #{
        type: "/problems/salary-indexation-conflict",
        title: "Salary indexation conflict",
        status: 409,
        detail: "Indexation would produce a salary outside the allowed range.",
        organization_id: 42
    }
};

@error
@doc("Результат индексации нарушает ограничения зарплаты.")
@example(salaryIndexationConflictErrorExample)
model SalaryIndexationConflictError is ProblemResponse<
    409,
    {
        ...OmitProperties<ProblemDetails<409>, "type">;

        type: "/problems/salary-indexation-conflict";

        @doc("ID организации.")
        @minValue(1)
        organization_id: int32;
    }
>;