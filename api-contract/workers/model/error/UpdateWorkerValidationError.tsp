import "../../../common/error/ValidationError.tsp";
import "./UpdateWorkerValidationIssue.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Workers.Errors;

const updateWorkerValidationExample: UpdateWorkerValidationError = #{
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
                field: "organization_id",
                code: "MUST_BE_POSITIVE",
                message: "organization_id must be greater than zero."
            }
        ]
    }
};

const updateWorkerReferenceValidationExample: UpdateWorkerValidationError = #{
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

const updateWorkerReferenceValidationOperationExample = #{
    returnType: updateWorkerReferenceValidationExample
};

@error
@doc("Ошибка валидации работника.")
@example(updateWorkerValidationExample)
@example(updateWorkerReferenceValidationExample)
model UpdateWorkerValidationError is ValidationError<UpdateWorkerValidationIssue>;
