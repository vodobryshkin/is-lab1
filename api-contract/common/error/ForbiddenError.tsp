import "./ProblemResponse.tsp";

namespace WorkersApi.Errors;

const forbiddenExample: ForbiddenError = #{
    statusCode: 403,
    contentType: "application/json",
    body: #{
        type: "/problems/invalid-csrf-token",
        title: "Invalid CSRF token",
        status: 403,
        detail: "The X-CSRF-Token header is missing or does not match the current session.",
        instance: "/problem-occurrences/example-403"
    }
};

const forbiddenOperationExample = #{
    returnType: forbiddenExample
};

@error
@doc("""
    Ошибка: изменяющий запрос не прошёл проверку CSRF. Заголовок `X-CSRF-Token`
    отсутствует или не совпадает с токеном текущей сессии. Операция не выполняется.
    """)
@example(forbiddenExample)
model ForbiddenError is ProblemResponse<403>;
