import "./ProblemDetails.tsp";
import "./ValidationIssue.tsp";

namespace WorkersApi.Errors;

@doc("Проблема обработки содержимого с подробностями валидации.")
model ValidationProblemDetails {
    ...ProblemDetails<422>;

    @doc("Нарушения правил валидации, если доступны.")
    @minItems(1)
    errors?: ValidationIssue[];
}