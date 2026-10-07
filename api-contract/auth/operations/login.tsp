import "@typespec/http";

import "../model/request/LoginRequest.tsp";
import "../model/response/TokenResponse.tsp";
import "../model/error/AuthValidationIssue.tsp";

import "../../common/ok/OkResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Errors;
using WorkersApi.Auth.Errors;

namespace WorkersApi.Auth;

const loginRefreshCookieExample =
    "refresh_token=e134aff8-7826-45b0-9b72-a5daa36b56c5; Path=/api/v1/auth; Max-Age=604800; HttpOnly; Secure; SameSite=Strict";

const loginSuccessOperationExample = #{
    parameters: #{ body: loginRequestExample },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        set_cookie: loginRefreshCookieExample,
        cache_control: "no-store",
        body: tokenResponseExample
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
@doc("Возвращает access-токен и устанавливает refresh-cookie.")
@returnsDoc("Access-токен и refresh-cookie.")
@opExample(loginSuccessOperationExample)
@opExample(authBadRequestOperationExample)
@opExample(loginInvalidCredentialsOperationExample)
@opExample(credentialsValidationOperationExample)
op login(
    @body body: LoginRequest
):
    {
        ...WorkersApi.Ok.OkResponse<TokenResponse>;

        @doc("Refresh-cookie: HttpOnly, Secure, SameSite=Strict, Path=/api/v1/auth.")
        @header("Set-Cookie")
        @example(loginRefreshCookieExample)
        set_cookie: string;

        @header("Cache-Control")
        cache_control: "no-store";
    }
    | BadRequestError
    | ValidationError<422, AuthValidationIssue>;