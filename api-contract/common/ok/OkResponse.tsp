import "@typespec/http";

using TypeSpec.Http;

namespace WorkersApi.Ok;

@doc("Запрос успешно выполнен.")
model OkResponse<TBody> {
    @statusCode
    statusCode: 200;

    @header("Content-Type")
    contentType: "application/json";

    @body
    body: TBody;
}