import "@typespec/http";

import "../model/response/CsrfTokenResponse.tsp";

import "../../common/ok/OkResponse.tsp";

using TypeSpec.Http;

namespace WorkersApi.Auth;

const anonymousSessionCookieExample =
    "JSESSIONID=hY3kQ9vT2mLx8RcW1pN6aB4sD7fG0jZe; Path=/; HttpOnly; SameSite=Lax";

const getCsrfTokenNewSessionOperationExample = #{
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        cache_control: "no-store",
        set_cookie: anonymousSessionCookieExample,
        body: csrfTokenResponseExample
    }
};

const getCsrfTokenExistingSessionOperationExample = #{
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        cache_control: "no-store",
        body: csrfTokenResponseExample
    }
};

@get
@route("/csrf")
@summary("Получить CSRF-токен.")
@doc("""
    Возвращает CSRF-токен текущей сессии. Если сессии нет, сервер создаёт анонимную сессию
    и устанавливает cookie `JSESSIONID`. В пределах одной сессии возвращается один и тот же
    токен; после входа и выхода токен заменяется, и его нужно получить заново.
    Вызов не требует входа и не изменяет предметные данные.
    """)
@returnsDoc("CSRF-токен для заголовка `X-CSRF-Token`.")
@opExample(getCsrfTokenNewSessionOperationExample)
@opExample(getCsrfTokenExistingSessionOperationExample)
op getCsrfToken():
    {
        ...WorkersApi.Ok.OkResponse<CsrfTokenResponse>;

        @header("Cache-Control")
        cache_control: "no-store";

        @doc("Cookie новой анонимной сессии; отсутствует, если сессия уже есть.")
        @header("Set-Cookie")
        @example(anonymousSessionCookieExample)
        set_cookie?: string;
    };
