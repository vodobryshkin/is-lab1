import "../../../common/error/ProblemResponse.tsp";
import "../../../common/error/ValidationProblemDetails.tsp";
import "./UpdateWorkerValidationIssue.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Workers.Errors;

const updateWorkerValidationExample: UpdateWorkerValidationError = #{
    statusCode: 422,
    contentType: "application/json",
    body: #{
        type: "/problems/update-worker-validation",
        title: "Validation failed",
        status: 422,
        errors: #[
            #{
                field: "salary",
                title: "MUST_BE_POSITIVE",
                description: "Salary must be greater than zero."
            },
            #{
                field: "organization_id",
                title: "REQUIRED",
                description: "Organization ID must be provided. Use null to unlink the organization."
            }
        ]
    }
};

@error
@doc("Ошибка валидации работника.")
@example(updateWorkerValidationExample)
model UpdateWorkerValidationError is ProblemResponse<
    422,
    {
        ...OmitProperties<ValidationProblemDetails, "type" | "errors">;

        type: "/problems/update-worker-validation";

        @minItems(1)
        errors: UpdateWorkerValidationIssue[];
    }
>;