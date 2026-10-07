import "@typespec/http";

import "../../common/ok/NoContentResponse.tsp";

using TypeSpec.Http;

namespace WorkersApi.Auth;

const logoutCookieValueExample = "e134aff8-7826-45b0-9b72-a5daa36b56c5";

const clearedRefreshCookieExample =
    "refresh_token=; Path=/api/v1/auth; Max-Age=0; HttpOnly; Secure; SameSite=Strict";

const logoutSuccessOperationExample = #{
    parameters: #{ refresh_token: logoutCookieValueExample },
    returnType: #{
        statusCode: 204,
        set_cookie: clearedRefreshCookieExample,
        cache_control: "no-store"
    }
};

const logoutWithoutCookieOperationExample = #{
    parameters: #{},
    returnType: #{
        statusCode: 204,
        set_cookie: clearedRefreshCookieExample,
        cache_control: "no-store"
    }
};

@post
@route("/logout")
@summary("Выйти из системы.")
@doc("Отзывает refresh-токен и удаляет cookie. Отсутствующий или недействительный токен также возвращает 204.")
@returnsDoc("Сессия завершена, cookie удалена.")
@opExample(logoutSuccessOperationExample)
@opExample(logoutWithoutCookieOperationExample)
op logout(
    @doc("Refresh-токен из cookie, если есть.")
    @cookie("refresh_token")
    @secret
    @example(logoutCookieValueExample)
    refresh_token?: string
):
    {
        ...WorkersApi.Ok.NoContentResponse;

        @doc("Удаление refresh-cookie.")
        @header("Set-Cookie")
        @example(clearedRefreshCookieExample)
        set_cookie: string;

        @header("Cache-Control")
        cache_control: "no-store";
    };