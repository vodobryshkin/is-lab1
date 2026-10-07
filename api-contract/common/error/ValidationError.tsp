import "./ProblemResponse.tsp";
import "./ValidationIssue.tsp";
import "./ParameterValidationIssue.tsp";

namespace WorkersApi.Errors;

const parameterValidationErrorExample:
    ValidationError<400, ParameterValidationIssue> = #{
        statusCode: 400,
        contentType: "application/json",
        body: #{
            type: "/problems/validation-failed",
            title: "Invalid request parameters",
            status: 400,
            errors: #[parameterValidationIssueExample]
        }
    };

const validationErrorExample: ValidationError<422, ValidationIssue> = #{
    statusCode: 422,
    contentType: "application/json",
    body: #{
        type: "/problems/validation-failed",
        title: "Validation failed",
        status: 422,
        errors: #[
            #{
                field: "salary",
                title: "MUST_BE_POSITIVE",
                description: "Salary must be greater than zero."
            }
        ]
    }
};

@error
@doc("Ошибка валидации запроса.")
model ValidationError<Status extends 400 | 422, TIssue extends ValidationIssue> is
    ProblemResponse<
        Status,
        {
            ...OmitProperties<ProblemDetails<Status>, "type">;

            type: "/problems/validation-failed";

            @doc("Ошибки полей.")
            @minItems(1)
            errors: TIssue[];
        }
    >;