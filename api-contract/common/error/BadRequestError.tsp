import "./ProblemResponse.tsp";

namespace WorkersApi.Errors;

@error
@doc("Ошибка: запрос невозможно обработать.")
model BadRequestError is ProblemResponse<400>;