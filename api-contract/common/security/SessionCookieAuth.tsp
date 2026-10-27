import "@typespec/http";

using TypeSpec.Http;

namespace WorkersApi.Security;

@doc("""
    Серверная HTTP-сессия в cookie `JSESSIONID`. Cookie выдаёт сервер при `GET /auth/csrf`,
    а после входа заменяет её новой. Cookie имеет атрибуты `HttpOnly`, `SameSite=Lax`, `Path=/`,
    а при размещении по HTTPS -- также `Secure`. Без входа защищённые операции возвращают 401.
    Изменяющие запросы дополнительно требуют заголовок `X-CSRF-Token` текущей сессии.
    """)
model SessionCookieAuth is ApiKeyAuth<ApiKeyLocation.cookie, "JSESSIONID">;
