import "./ProblemResponse.tsp";

namespace WorkersApi.Errors;

const unauthorizedExample: UnauthorizedError = #{
    statusCode: 401,
    contentType: "application/json",
    body: #{
        type: "/problems/authentication-required",
        title: "Authentication required",
        status: 401,
        detail: "Log in to access this resource.",
        instance: "/problem-occurrences/example-401"
    }
};

const unauthorizedOperationExample = #{
    returnType: unauthorizedExample
};

@error
@doc("""
    Ошибка: нет действующего входа. Сессия отсутствует, истекла или завершена выходом.
    Ответ не содержит `WWW-Authenticate`: для cookie-сессии нет стандартной HTTP-схемы
    challenge, клиент выполняет вход через `POST /auth/login`.
    """)
@example(unauthorizedExample)
model UnauthorizedError is ProblemResponse<401>;
