import "./ProblemResponse.tsp";

using TypeSpec.Http;

namespace WorkersApi.Errors;

const forbiddenExample: ForbiddenError = #{
    statusCode: 403,
    contentType: "application/json",
    body: #{
        type: "/problems/insufficient-permissions",
        title: "Недостаточно прав",
        status: 403,
        detail: "У пользователя нет права изменять этого работника.",
        instance: "/problem-occurrences/example-403"
    }
};

@error
@doc("Ошибка: сервер отказывается выполнять запрос из-за недостатка прав.")
@example(forbiddenExample)
model ForbiddenError {
    ...ProblemResponse<403>;

    @header("WWW-Authenticate")
    wwwAuthenticate?: string;
}