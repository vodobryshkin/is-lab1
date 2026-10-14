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

    **WebSocket:** `ws://localhost:8080/api/v1/events` (вне локальной разработки — `wss`).
    Первое сообщение клиента: `{"type":"AUTHENTICATE","access_token":"<access_token>"}`.
    Сервер подтверждает вход сообщением `{"type":"AUTHENTICATED"}`.
    После фиксации изменений сервер рассылает `{"type":"DATA_CHANGED"}`; клиент перечитывает данные через REST.
    При переподключении данные загружаются заново.
    [Полный контракт WebSocket (AsyncAPI)](https://github.com/vodobryshkin/is-lab1/blob/main/api-contract/events/asyncapi.yaml).
    """)
@externalDocs(
    "https://github.com/vodobryshkin/is-lab1/blob/main/api-contract/events/asyncapi.yaml",
    "WebSocket: сообщения, ошибки и примеры (AsyncAPI)."
)
@server("http://localhost:8080/api/v1", "Локальный сервер")
@useAuth(BearerAuth)
namespace WorkersApi;
