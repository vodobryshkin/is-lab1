import "./ProblemResponse.tsp";
import "./ValidationIssue.tsp";
import "./ParameterValidationIssue.tsp";

namespace WorkersApi.Errors;

const listParameterValidationExample:
    ValidationError<400, ParameterValidationIssue> = #{
        statusCode: 400,
        contentType: "application/json",
        body: #{
            type: "/problems/validation-failed",
            title: "Invalid request parameters",
            status: 400,
            errors: #[
                #{
                    field: "page",
                    title: "MUST_BE_POSITIVE",
                    description: "Page must be greater than zero."
                },
                #{
                    field: "page_size",
                    title: "OUT_OF_RANGE",
                    description: "Page size must not exceed 100."
                },
                #{
                    field: "sort_by",
                    title: "INVALID_ENUM_VALUE",
                    description: "The sort field is not supported."
                },
                #{
                    field: "sort_order",
                    title: "INVALID_ENUM_VALUE",
                    description: "Sort order must be asc or desc."
                }
            ]
        }
    };

const listParameterValidationOperationExample = #{
    returnType: listParameterValidationExample
};

const invalidIdExample: ValidationError<400, ParameterValidationIssue> = #{
    statusCode: 400,
    contentType: "application/json",
    body: #{
        type: "/problems/validation-failed",
        title: "Invalid request parameters",
        status: 400,
        errors: #[
            #{
                field: "id",
                title: "MUST_BE_POSITIVE",
                description: "ID must be greater than zero."
            }
        ]
    }
};

const invalidIdOperationExample = #{
    returnType: invalidIdExample
};

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
