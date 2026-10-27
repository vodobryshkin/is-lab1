import "@typespec/http";
import "./ValidationIssue.tsp";

using TypeSpec.Http;

namespace WorkersApi.Errors;

@doc("Нарушения полей тела запроса.")
model ValidationErrorResponse<TIssue extends ValidationIssue = ValidationIssue> {
    @doc("Все найденные нарушения; порядок не задан.")
    @minItems(1)
    errors: TIssue[];
}

@error
@doc("Ошибка валидации тела запроса.")
model ValidationError<TIssue extends ValidationIssue = ValidationIssue> {
    @statusCode
    statusCode: 422;

    @header("Content-Type")
    contentType: "application/json";

    @body
    body: ValidationErrorResponse<TIssue>;
}
