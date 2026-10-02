import "@typespec/http";
import "./ProblemDetails.tsp";

using TypeSpec.Http;

namespace WorkersApi.Errors;

@error
@doc("Ошибка: отсутствуют действительные данные аутентификации.")
model UnauthorizedError {
    @statusCode
    statusCode: 401;

    @header("WWW-Authenticate")
    wwwAuthenticate: string;

    @header("Content-Type")
    contentType: "application/problem+json";

    @body
    body?: ProblemDetails<401>;
}