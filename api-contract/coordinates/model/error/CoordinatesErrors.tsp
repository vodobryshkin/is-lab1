import "../../../common/error/BadRequestError.tsp";
import "../../../common/error/UnauthorizedError.tsp";
import "../../../common/error/ForbiddenError.tsp";
import "../../../common/error/NotFoundError.tsp";
import "../../../common/error/ConflictError.tsp";
import "../../../common/error/ValidationError.tsp";
import "../../../common/error/validation/CoordinatesValidationIssue.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Coordinates.Errors;

const coordinatesValidationErrorExample: CoordinatesValidationError = #{
    statusCode: 422,
    contentType: "application/json",
    body: #{
        errors: #[
            #{
                field: "x",
                code: "MUST_BE_GREATER_THAN_MINUS_954",
                message: "x must be greater than -954."
            },
            #{
                field: "y",
                code: "MUST_BE_GREATER_THAN_MINUS_392",
                message: "y must be greater than -392."
            }
        ]
    }
};

const coordinatesValidationOperationExample = #{
    returnType: coordinatesValidationErrorExample
};

const deleteCoordinatesConflictErrorExample: ConflictError = #{
    statusCode: 409,
    contentType: "application/json",
    body: #{
        message: "The coordinates cannot be deleted while workers reference them."
    }
};

const deleteCoordinatesConflictOperationExample = #{
    returnType: deleteCoordinatesConflictErrorExample
};

@doc("Ошибки данных координат.")
union CoordinatesRequestValidationIssue {
    coordinates: CoordinatesValidationIssue<"", "">,

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
@doc("Ошибка валидации координат.")
@example(coordinatesValidationErrorExample)
model CoordinatesValidationError is
    ValidationError<CoordinatesRequestValidationIssue>;
