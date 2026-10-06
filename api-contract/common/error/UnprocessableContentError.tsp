import "./ProblemResponse.tsp";
import "./ValidationProblemDetails.tsp";

namespace WorkersApi.Errors;

const unprocessableContentExample: UnprocessableContentError = #{
    statusCode: 422,
    contentType: "application/json",
    body: validationProblemDetailsExample
};

@error
@doc("Ошибка: синтаксис содержимого корректен, но обработка невозможна из-за причин валидации.")
@example(unprocessableContentExample)
model UnprocessableContentError
    is ProblemResponse<422, ValidationProblemDetails>;