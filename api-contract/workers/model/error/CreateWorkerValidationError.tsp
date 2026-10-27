import "../../../common/error/ValidationError.tsp";
import "./CreateWorkerValidationIssue.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Workers.Errors;

const createWorkerValidationExample: CreateWorkerValidationError = #{
    statusCode: 422,
    contentType: "application/json",
    body: #{
        errors: #[
            #{
                field: "salary",
                code: "MUST_BE_POSITIVE",
                message: "salary must be greater than zero."
            },
            #{
                field: "coordinates.x",
                code: "MUST_BE_GREATER_THAN_MINUS_954",
                message: "coordinates.x must be greater than -954."
            }
        ]
    }
};

const createWorkerReferenceValidationExample: CreateWorkerValidationError = #{
    statusCode: 422,
    contentType: "application/json",
    body: #{
        errors: #[
            #{
                field: "coordinates.id",
                code: "MUST_BE_POSITIVE",
                message: "coordinates.id must be greater than zero."
            },
            #{
                field: "person.location.id",
                code: "MUST_BE_POSITIVE",
                message: "person.location.id must be greater than zero."
            }
        ]
    }
};

const createWorkerReferenceValidationOperationExample = #{
    returnType: createWorkerReferenceValidationExample
};

@error
@doc("Ошибка валидации работника.")
@example(createWorkerValidationExample)
@example(createWorkerReferenceValidationExample)
model CreateWorkerValidationError is ValidationError<CreateWorkerValidationIssue>;
