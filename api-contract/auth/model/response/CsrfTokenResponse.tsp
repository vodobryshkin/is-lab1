import "../../../common/security/CsrfTokenHeader.tsp";

using WorkersApi.Security;

namespace WorkersApi.Auth;

const csrfTokenResponseExample: CsrfTokenResponse = #{
    csrf_token: csrfTokenExample
};

@doc("CSRF-токен текущей сессии.")
@example(csrfTokenResponseExample)
model CsrfTokenResponse {
    @doc("Значение для заголовка `X-CSRF-Token`. Не совпадает с ID сессии.")
    @secret
    @minLength(1)
    csrf_token: string;
}
