import "@typespec/http";
import "./ProblemDetails.tsp";

using TypeSpec.Http;

namespace WorkersApi.Errors;

const unauthorizedExample: UnauthorizedError = #{
    statusCode: 401,
    wwwAuthenticate: "Bearer realm=\"Workers API\", error=\"invalid_token\"",
    contentType: "application/problem+json",
    body: #{
        type: "/problems/invalid-token",
        title: "Недействительный токен",
        status: 401,
        detail: "Срок действия токена истёк. Пройдите аутентификацию повторно.",
        instance: "/problem-occurrences/example-401"
    }
};

@error
@doc("Ошибка: отсутствуют действительные данные аутентификации.")
@example(unauthorizedExample)
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