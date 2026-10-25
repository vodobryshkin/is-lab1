import "@typespec/http";

using TypeSpec.Http;

namespace WorkersApi.Security;

const csrfTokenExample = "k8Jm2xQ4sV1nR7tY0wZ3bC6dF9gH2jL5";

@doc("Заголовок с CSRF-токеном для изменяющих запросов.")
model CsrfTokenHeader {
    @doc("""
        CSRF-токен текущей сессии из `GET /auth/csrf`. Отсутствующий токен
        или токен другой сессии отклоняется ответом 403 до выполнения операции.
        """)
    @header("X-CSRF-Token")
    @secret
    @minLength(1)
    @example(csrfTokenExample)
    csrf_token: string;
}
