import "@typespec/http";

import "../model/request/LoginRequest.tsp";
import "../model/response/CurrentUserResponse.tsp";
import "../model/error/AuthValidationIssue.tsp";

import "../../common/error/ForbiddenError.tsp";
import "../../common/ok/OkResponse.tsp";
import "../../common/security/CsrfTokenHeader.tsp";

using TypeSpec.Http;
using WorkersApi.Errors;
using WorkersApi.Security;
using WorkersApi.Auth.Errors;

namespace WorkersApi.Auth;

const loginSessionCookieExample =
    "JSESSIONID=Vb5nM1qX8tK3wR6yP0cL9dF2gH4jS7aZ; Path=/; HttpOnly; SameSite=Lax";

const loginSuccessOperationExample = #{
    parameters: #{ csrf_token: csrfTokenExample, body: loginRequestExample },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        set_cookie: loginSessionCookieExample,
        cache_control: "no-store",
        body: currentUserResponseExample
    }
};

const loginInvalidCredentialsExample: BadRequestError = #{
    statusCode: 400,
    contentType: "application/json",
    body: #{
        type: "/problems/invalid-credentials",
        title: "Invalid credentials",
        status: 400,
        detail: "Username or password is incorrect."
    }
};

const loginInvalidCredentialsOperationExample = #{
    returnType: loginInvalidCredentialsExample
};

@post
@route("/login")
@summary("Войти в систему.")
@doc("""
    Проверяет логин и пароль и связывает вход с серверной сессией. Сначала сервер проверяет
    CSRF-токен текущей сессии и только затем учётные данные.
    При успехе сервер меняет ID сессии и устанавливает новую cookie `JSESSIONID`;
    прежний ID сессии больше не даёт доступа. CSRF-токен тоже заменяется:
    перед следующим изменяющим запросом получите его через `GET /auth/csrf`.
    Повторный вход в той же сессии заменяет текущего пользователя.
    """)
@returnsDoc("Вошедший пользователь и cookie новой сессии.")
@opExample(loginSuccessOperationExample)
@opExample(authBadRequestOperationExample)
@opExample(loginInvalidCredentialsOperationExample)
@opExample(forbiddenOperationExample)
@opExample(credentialsValidationOperationExample)
op login(
    ...CsrfTokenHeader,
    @body body: LoginRequest
):
    {
        ...WorkersApi.Ok.OkResponse<CurrentUserResponse>;

        @doc("Cookie новой сессии: HttpOnly, SameSite=Lax, Path=/; при HTTPS также Secure.")
        @header("Set-Cookie")
        @example(loginSessionCookieExample)
        set_cookie: string;

        @header("Cache-Control")
        cache_control: "no-store";
    }
    | BadRequestError
    | ForbiddenError
    | ValidationError<422, AuthValidationIssue>;
