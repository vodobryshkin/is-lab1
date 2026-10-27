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
@externalDocs(
    "https://github.com/vodobryshkin/is-lab1/blob/main/api-contract/events/asyncapi.yaml",
    "WebSocket: сообщения, ошибки и примеры (AsyncAPI)."
)
@server("http://localhost:8080/api/v1", "Локальный сервер")
@useAuth(WorkersApi.Security.SessionCookieAuth)
namespace WorkersApi;
