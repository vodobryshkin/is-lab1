import "../../../common/error/BadRequestError.tsp";
import "../../../common/error/UnauthorizedError.tsp";
import "../../../common/error/ForbiddenError.tsp";
import "../../../common/error/NotFoundError.tsp";
import "../../../common/error/ConflictError.tsp";
import "../../../common/error/ValidationError.tsp";
import "../../../common/error/validation/PersonValidationIssue.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Persons.Errors;

const personValidationErrorExample: PersonValidationError = #{
    statusCode: 422,
    contentType: "application/json",
    body: #{
        errors: #[
            #{
                field: "weight",
                code: "MUST_BE_POSITIVE",
                message: "weight must be greater than zero."
            },
            #{
                field: "hair_color",
                code: "MUST_NOT_BE_NULL",
                message: "hair_color must not be null."
            },
            #{
                field: "location.id",
                code: "MUST_BE_POSITIVE",
                message: "location.id must be greater than zero."
            }
        ]
    }
};

const personValidationOperationExample = #{
    returnType: personValidationErrorExample
};

const deletePersonConflictErrorExample: ConflictError = #{
    statusCode: 409,
    contentType: "application/json",
    body: #{
        message: "The person cannot be deleted while a worker references it."
    }
};

const deletePersonConflictOperationExample = #{
    returnType: deletePersonConflictErrorExample
};

@doc("Ошибки данных персоны.")
union PersonRequestValidationIssue {
    person: PersonValidationIssue<"", "">,
}

@error
@doc("Ошибка валидации персоны.")
@example(personValidationErrorExample)
model PersonValidationError is
    ValidationError<PersonRequestValidationIssue>;
