import "./ProblemResponse.tsp";

using TypeSpec.Http;

namespace WorkersApi.Errors;

@error
@doc("Ошибка: сервер отказывается выполнять запрос из-за недостатка прав.")
model ForbiddenError {
    ...ProblemResponse<403>;

    @header("WWW-Authenticate")
    wwwAuthenticate?: string;
}