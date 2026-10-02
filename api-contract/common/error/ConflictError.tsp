import "./ProblemResponse.tsp";

namespace WorkersApi.Errors;

@error
@doc("Ошибка: конфликт с текущим состоянием ресурса.")
model ConflictError is ProblemResponse<409>;