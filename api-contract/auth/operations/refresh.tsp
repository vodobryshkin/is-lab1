import "@typespec/http";

import "../model/response/TokenResponse.tsp";
import "../model/error/AuthValidationIssue.tsp";

import "../../common/ok/OkResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Errors;
using WorkersApi.Auth.Errors;

namespace WorkersApi.Auth;

const refreshCookieValueExample = "e134aff8-7826-45b0-9b72-a5daa36b56c5";

const rotatedRefreshCookieExample =
    "refresh_token=eac469ab-dba4-4d13-978a-785c7d4db5fd; Path=/api/v1/auth; Max-Age=604800; HttpOnly; Secure; SameSite=Strict";

const refreshSuccessOperationExample = #{
    parameters: #{ refresh_token: refreshCookieValueExample },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        set_cookie: rotatedRefreshCookieExample,
        cache_control: "no-store",
        body: refreshedTokenResponseExample
    }
};

const refreshInvalidTokenExample: BadRequestError = #{
    statusCode: 400,
    contentType: "application/json",
    body: #{
        type: "/problems/invalid-refresh-token",
        title: "Invalid refresh token",
        status: 400,
        detail: "The refresh token is invalid, expired or revoked."
    }
};

const refreshInvalidTokenOperationExample = #{
    returnType: refreshInvalidTokenExample
};

@post
@route("/refresh")
@summary("Обновить токены.")
@doc("Возвращает access-токен, заменяет refresh-cookie и отзывает использованный refresh-токен.")
@returnsDoc("Новый access-токен и refresh-cookie.")
@opExample(refreshSuccessOperationExample)
@opExample(refreshInvalidTokenOperationExample)
@opExample(refreshTokenValidationOperationExample)
op refresh(
    @doc("Refresh-токен из cookie.")
    @cookie("refresh_token")
    @secret
    @minLength(1)
    @example(refreshCookieValueExample)
    refresh_token: string
):
    {
        ...WorkersApi.Ok.OkResponse<TokenResponse>;

        @doc("Новая refresh-cookie: HttpOnly, Secure, SameSite=Strict, Path=/api/v1/auth.")
        @header("Set-Cookie")
        @example(rotatedRefreshCookieExample)
        set_cookie: string;

        @header("Cache-Control")
        cache_control: "no-store";
    }
    | BadRequestError
    | ValidationError<422, AuthValidationIssue>;