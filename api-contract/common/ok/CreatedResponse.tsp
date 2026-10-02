import "@typespec/http";

using TypeSpec.Http;

namespace WorkersApi.Ok;

@doc("Ресурс успешно создан")
model CreatedResponse<TBody> {
    @statusCode
    statusCode: 201;

    @header("Content-Type")
    contentType: "application/json";

    @doc("URI созданного ресурса.")
    @header("Location")
    location?: string;

    @body
    body: TBody;
}