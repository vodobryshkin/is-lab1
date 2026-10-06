import "./ProblemDetails.tsp";
import "./ValidationIssue.tsp";

namespace WorkersApi.Errors;

const validationProblemDetailsExample: ValidationProblemDetails = #{
    type: "/problems/validation-failed",
    title: "Data cannot pass the validation.",
    status: 422,
    detail: "Solve the problems below.",
    instance: "/problem-occurrences/example-422",
    errors: #[
        validationIssueExample,
        #{
            field: "rating",
            title: "RATING_MUST_BE_POSITIVE",
            description: "Rating must be positive."
        }
    ]
};

@doc("Проблема обработки содержимого с подробностями валидации.")
@example(validationProblemDetailsExample)
model ValidationProblemDetails {
    ...ProblemDetails<422>;

    @doc("Нарушения правил валидации, если доступны.")
    @minItems(1)
    errors?: ValidationIssue[];
}