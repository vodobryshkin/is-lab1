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
        type: "/problems/validation-failed",
        title: "Validation failed",
        status: 422,
        errors: #[
            #{
                field: "weight",
                title: "MUST_BE_POSITIVE",
                description: "Weight must be greater than zero."
            },
            #{
                field: "hair_color",
                title: "MUST_NOT_BE_NULL",
                description: "Hair color must not be null."
            },
            #{
                field: "location.id",
                title: "MUST_BE_POSITIVE",
                description: "Location ID must be greater than zero."
            }
        ]
    }
};

const personValidationOperationExample = #{
    returnType: personValidationErrorExample
};

const deletePersonConflictErrorExample: DeletePersonConflictError = #{
    statusCode: 409,
    contentType: "application/json",
    body: #{
        type: "/problems/person-is-in-use",
        title: "Person is in use",
        status: 409,
        detail: "The person cannot be deleted while a worker references it.",
        person_id: 10
    }
};

const deletePersonConflictOperationExample = #{
    returnType: deletePersonConflictErrorExample
};

@doc("Ошибки данных персоны.")
union PersonRequestValidationIssue {
    person: PersonValidationIssue<"", "">,

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
@doc("Ошибка валидации персоны.")
@example(personValidationErrorExample)
model PersonValidationError is
    ValidationError<422, PersonRequestValidationIssue>;

@error
@doc("Персона используется связанными объектами.")
@example(deletePersonConflictErrorExample)
model DeletePersonConflictError is ConflictError<
    {
        ...OmitProperties<ProblemDetails<409>, "type">;

        type: "/problems/person-is-in-use";

        @doc("ID персоны.")
        @minValue(1)
        person_id: int64;
    }
>;
