import "./ErrorResponse.tsp";

namespace WorkersApi.Errors;

const unauthorizedExample: UnauthorizedError = #{
    statusCode: 401,
    contentType: "application/json",
    body: #{
        message: "Log in to access this resource."
    }
};

const unauthorizedOperationExample = #{
    returnType: unauthorizedExample
};

@error
@doc("""
    Ошибка: нет действующего входа. Сессия отсутствует, истекла или завершена выходом.
    Ответ не содержит `WWW-Authenticate`: клиент выполняет вход через `POST /auth/login`.
    """)
@example(unauthorizedExample)
model UnauthorizedError is ErrorResult<401>;
