import "@typespec/http";

using TypeSpec.Http;

namespace WorkersApi.Errors;

@doc("Описание ошибки. HTTP-статус передаётся в самом ответе.")
model ErrorResponse {
    @doc("Понятное описание причины ошибки.")
    message: string;
}

@error
model ErrorResult<Status extends int32> {
    @statusCode
    statusCode: Status;

    @header("Content-Type")
    contentType: "application/json";

    @body
    body: ErrorResponse;
}
