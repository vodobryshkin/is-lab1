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
        type: "/problems/validation-failed",
        title: "Validation failed",
        status: 422,
        errors: #[
            #{
                field: "x",
                title: "MUST_BE_GREATER_THAN_MINUS_954",
                description: "X must be greater than -954."
            },
            #{
                field: "y",
                title: "MUST_BE_GREATER_THAN_MINUS_392",
                description: "Y must be greater than -392."
            }
        ]
    }
};

const coordinatesValidationOperationExample = #{
    returnType: coordinatesValidationErrorExample
};

const deleteCoordinatesConflictErrorExample: DeleteCoordinatesConflictError = #{
    statusCode: 409,
    contentType: "application/json",
    body: #{
        type: "/problems/coordinates-are-in-use",
        title: "Coordinates are in use",
        status: 409,
        detail: "The coordinates cannot be deleted while workers reference them.",
        coordinates_id: 5
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
@doc("Ошибка валидации координат.")
@example(coordinatesValidationErrorExample)
model CoordinatesValidationError is
    ValidationError<422, CoordinatesRequestValidationIssue>;

@error
@doc("Координаты используются связанными объектами.")
@example(deleteCoordinatesConflictErrorExample)
model DeleteCoordinatesConflictError is ConflictError<
    {
        ...OmitProperties<ProblemDetails<409>, "type">;

        type: "/problems/coordinates-are-in-use";

        @doc("ID координат.")
        @minValue(1)
        coordinates_id: int64;
    }
>;
