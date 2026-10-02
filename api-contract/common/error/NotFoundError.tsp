import "./ProblemResponse.tsp";

namespace WorkersApi.Errors;

@error
@doc("Ошибка: ресурс не найден либо его существование скрыто.")
model NotFoundError is ProblemResponse<404>;