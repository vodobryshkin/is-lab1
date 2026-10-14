import "../../../common/error/ProblemResponse.tsp";
import "../../../common/error/ValidationProblemDetails.tsp";
import "./CreateWorkerValidationIssue.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Workers.Errors;

const createWorkerValidationExample: CreateWorkerValidationError = #{
    statusCode: 422,
    contentType: "application/json",
    body: #{
        type: "/problems/create-worker-validation",
        title: "Validation failed",
        status: 422,
        errors: #[
            #{
                field: "salary",
                title: "MUST_BE_POSITIVE",
                description: "Salary must be greater than zero."
            },
            #{
                field: "coordinates.x",
                title: "MUST_BE_GREATER_THAN_MINUS_954",
                description: "x must be greater than -954."
            }
        ]
    }
};

const createWorkerReferenceValidationExample: CreateWorkerValidationError = #{
    statusCode: 422,
    contentType: "application/json",
    body: #{
        type: "/problems/create-worker-validation",
        title: "Validation failed",
        status: 422,
        errors: #[
            #{
                field: "coordinates.id",
                title: "MUST_BE_POSITIVE",
                description: "Coordinates ID must be greater than zero."
            },
            #{
                field: "person.location.id",
                title: "MUST_BE_POSITIVE",
                description: "Location ID must be greater than zero."
            }
        ]
    }
};

const createWorkerReferenceValidationOperationExample = #{
    returnType: createWorkerReferenceValidationExample
};

@example(createWorkerReferenceValidationExample)
@error
@doc("Ошибка валидации работника.")
@example(createWorkerValidationExample)
model CreateWorkerValidationError is ProblemResponse<
    422,
    {
        ...OmitProperties<
            ValidationProblemDetails,
            "type" | "errors"
        >;

        type: "/problems/create-worker-validation";

        @minItems(1)
        errors: CreateWorkerValidationIssue[];
    }
>;