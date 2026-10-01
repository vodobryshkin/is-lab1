import "@typespec/http";
import "@typespec/openapi3";

using TypeSpec.Http;

@service(#{ title: "Tasks API" })
@server("http://localhost:8080", "Локальный сервер")
namespace TasksApi;