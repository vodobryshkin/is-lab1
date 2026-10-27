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
                code: "OUT_OF_RANGE",
                message: "x must fit into int32."
            },
            #{
                field: "y",
                code: "OUT_OF_RANGE",
                message: "y must fit into int32."
            },
            #{
                field: "z",
                code: "OUT_OF_RANGE",
                message: "z must fit into int64."
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

    body: {
        field: "$";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE";
        message: string;
    },

    unknown_field: {
        field: string;
        code: "UNKNOWN_FIELD";
        message: string;
    },
}

@error
@doc("Ошибка валидации локации.")
@example(locationValidationErrorExample)
model LocationValidationError is
    ValidationError<LocationRequestValidationIssue>;
