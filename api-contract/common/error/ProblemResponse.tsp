import "@typespec/http";
import "./ProblemDetails.tsp";

using TypeSpec.Http;

namespace WorkersApi.Errors;

@error
model ProblemResponse<Status extends int32, TBody = ProblemDetails<Status>> {
    @statusCode
    statusCode: Status;

    @header("Content-Type")
    contentType: "application/json";

    @body
    body: TBody;
}