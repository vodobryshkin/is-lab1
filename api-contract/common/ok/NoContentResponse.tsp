import "@typespec/http";

using TypeSpec.Http;

namespace WorkersApi.Ok;

@doc("Запрос успешно выполнен, ответ без тела.")
model NoContentResponse {
    @statusCode
    statusCode: 204;
}