import "./ProblemResponse.tsp";
import "./ValidationProblemDetails.tsp";

namespace WorkersApi.Errors;

@error
@doc("Ошибка: синтаксис содержимого корректен, но обработка невозможна из-за причин валидации.")
model UnprocessableContentError is ProblemResponse<422, ValidationProblemDetails>;