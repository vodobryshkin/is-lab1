import "@typespec/http";

import "../../common/error/ForbiddenError.tsp";
import "../../common/ok/NoContentResponse.tsp";
import "../../common/security/CsrfTokenHeader.tsp";

using TypeSpec.Http;
using WorkersApi.Errors;
using WorkersApi.Security;

namespace WorkersApi.Auth;

const clearedSessionCookieExample =
    "JSESSIONID=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax";

const logoutSuccessOperationExample = #{
    parameters: #{ csrf_token: csrfTokenExample },
    returnType: #{
        statusCode: 204,
        set_cookie: clearedSessionCookieExample,
        cache_control: "no-store"
    }
};

@post
@route("/logout")
@summary("Выйти из системы.")
@doc("""
    Завершает вход и инвалидирует серверную сессию вместе с её CSRF-токеном,
    затем удаляет cookie `JSESSIONID`. Вход не обязателен: при действительном
    CSRF-токене анонимной сессии ответ также 204. Повторный вызов со старым токеном
    возвращает 403, потому что его сессия уже завершена; новый токен выдаёт `GET /auth/csrf`.
    """)
@returnsDoc("Сессия завершена, cookie удалена.")
@opExample(logoutSuccessOperationExample)
@opExample(forbiddenOperationExample)
op logout(
    ...CsrfTokenHeader
):
    {
        ...WorkersApi.Ok.NoContentResponse;

        @doc("Удаление cookie сессии.")
        @header("Set-Cookie")
        @example(clearedSessionCookieExample)
        set_cookie: string;

        @header("Cache-Control")
        cache_control: "no-store";
    }
    | ForbiddenError;
