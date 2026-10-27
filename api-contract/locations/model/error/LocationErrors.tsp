import "../../../common/error/BadRequestError.tsp";
import "../../../common/error/UnauthorizedError.tsp";
import "../../../common/error/ForbiddenError.tsp";
import "../../../common/error/NotFoundError.tsp";
import "../../../common/error/ConflictError.tsp";
import "../../../common/error/ValidationError.tsp";
import "../../../common/error/validation/LocationValidationIssue.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Locations.Errors;

const locationValidationErrorExample: LocationValidationError = #{
    statusCode: 422,
    contentType: "application/json",
    body: #{
        errors: #[
            #{
                field: "x",
                code: "MUST_NOT_BE_NULL",
                message: "x must not be null."
            },
            #{
                field: "y",
                code: "MUST_NOT_BE_NULL",
                message: "y must not be null."
            },
            #{
                field: "z",
                code: "MUST_NOT_BE_NULL",
                message: "z must not be null."
            }
        ]
    }
};

const locationValidationOperationExample = #{
    returnType: locationValidationErrorExample
};

const deleteLocationConflictErrorExample: ConflictError = #{
    statusCode: 409,
    contentType: "application/json",
    body: #{
        message: "The location cannot be deleted while addresses or persons reference it."
    }
};

const deleteLocationConflictOperationExample = #{
    returnType: deleteLocationConflictErrorExample
};

@doc("Ошибки данных локации.")
union LocationRequestValidationIssue {
    location: LocationValidationIssue<"", "">,
}

@error
@doc("Ошибка валидации локации.")
@example(locationValidationErrorExample)
model LocationValidationError is
    ValidationError<LocationRequestValidationIssue>;
