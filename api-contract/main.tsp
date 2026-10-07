import "@typespec/http";
import "@typespec/openapi";
import "@typespec/openapi3";

import "./common";
import "./auth";
import "./workers";
import "./organizations";
import "./addresses";

using TypeSpec.Http;
using TypeSpec.OpenAPI;

@service(#{ title: "Workers API" })
@info(#{ version: "0.1.0" })
@server("http://localhost:8080/api/v1", "Локальный сервер")
@useAuth(BearerAuth)
namespace WorkersApi;