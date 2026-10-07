import "@typespec/http";
import "@typespec/openapi3";

import "./common";
import "./auth";
import "./workers";
import "./organizations";

using TypeSpec.Http;

@service(#{ title: "Workers API" })
@server("http://localhost:8080/api/v1", "Локальный сервер")
@useAuth(BearerAuth)
namespace WorkersApi;