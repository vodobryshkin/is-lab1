import "@typespec/http";
import "@typespec/openapi";
import "@typespec/openapi3";

import "./common";
import "./auth";
import "./workers";
import "./organizations";
import "./addresses";
import "./coordinates";
import "./locations";
import "./persons";

using TypeSpec.Http;
using TypeSpec.OpenAPI;

@service(#{ title: "Workers API" })
@info(#{ version: "0.1.0" })
@doc("""
    Управление работниками и связанными объектами. ID работника — положительный int32 (SERIAL); ID остальных сущностей — положительные int64 (BIGSERIAL).

    **Аутентификация:** серверная HTTP-сессия в cookie `JSESSIONID`.
    1. `GET /auth/csrf` создаёт сессию при необходимости и возвращает CSRF-токен.
    2. `POST /auth/login` с заголовком `X-CSRF-Token` выполняет вход, меняет ID сессии и CSRF-токен.
    3. Новый CSRF-токен снова выдаёт `GET /auth/csrf`.
    Все операции, кроме `/auth/csrf`, `/auth/register`, `/auth/login` и `/auth/logout`, требуют входа:
    без него сервер возвращает 401. Все изменяющие запросы, включая публичные
    register, login и logout, требуют заголовок `X-CSRF-Token` текущей сессии:
    отсутствующий или чужой токен даёт 403, и операция не выполняется.
    Сессия хранится в памяти сервера и завершается после выхода, по тайм-ауту бездействия
    или при перезапуске сервера; после этого нужно войти заново.

    **WebSocket:** `ws://localhost:8080/api/v1/events` (вне локальной разработки — `wss`).
    Соединение использует ту же cookie `JSESSIONID`; без действующего входа подключение отклоняется.
    Сервер подтверждает подключение сообщением `{"type":"AUTHENTICATED"}`.
    После фиксации изменений сервер рассылает `{"type":"DATA_CHANGED"}`; клиент перечитывает данные через REST.
    Пропущенные события не воспроизводятся: при переподключении данные загружаются заново.
    [Полный контракт WebSocket (AsyncAPI)](https://github.com/vodobryshkin/is-lab1/blob/main/api-contract/events/asyncapi.yaml).
    """)
@externalDocs(
    "https://github.com/vodobryshkin/is-lab1/blob/main/api-contract/events/asyncapi.yaml",
    "WebSocket: сообщения, ошибки и примеры (AsyncAPI)."
)
@server("http://localhost:8080/api/v1", "Локальный сервер")
@useAuth(WorkersApi.Security.SessionCookieAuth)
namespace WorkersApi;
