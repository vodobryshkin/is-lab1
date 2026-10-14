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
        type: "/problems/validation-failed",
        title: "Validation failed",
        status: 422,
        errors: #[
            #{
                field: "x",
                title: "OUT_OF_RANGE",
                description: "X must be within the int32 range."
            },
            #{
                field: "y",
                title: "OUT_OF_RANGE",
                description: "Y must be within the int32 range."
            },
            #{
                field: "z",
                title: "OUT_OF_RANGE",
                description: "Z must be within the int64 range."
            }
        ]
    }
};

const locationValidationOperationExample = #{
    returnType: locationValidationErrorExample
};

const deleteLocationConflictErrorExample: DeleteLocationConflictError = #{
    statusCode: 409,
    contentType: "application/json",
    body: #{
        type: "/problems/location-is-in-use",
        title: "Location is in use",
        status: 409,
        detail: "The location cannot be deleted while addresses or persons reference it.",
        location_id: 7
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
@doc("Ошибка валидации локации.")
@example(locationValidationErrorExample)
model LocationValidationError is
    ValidationError<422, LocationRequestValidationIssue>;

@error
@doc("Локация используется связанными объектами.")
@example(deleteLocationConflictErrorExample)
model DeleteLocationConflictError is ConflictError<
    {
        ...OmitProperties<ProblemDetails<409>, "type">;

        type: "/problems/location-is-in-use";

        @doc("ID локации.")
        @minValue(1)
        location_id: int32;
    }
>;
