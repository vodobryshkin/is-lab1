import "./ProblemResponse.tsp";

using TypeSpec.Http;

namespace WorkersApi.Errors;

const forbiddenExample: ForbiddenError = #{
    statusCode: 403,
    contentType: "application/json",
    body: #{
        type: "/problems/insufficient-permissions",
        title: "Insufficient permissions",
        status: 403,
        detail: "Modifying this resource is not allowed.",
        instance: "/problem-occurrences/example-403"
    }
};

const forbiddenOperationExample = #{
    returnType: forbiddenExample
};

@error
@doc("Ошибка: сервер отказывается выполнять запрос из-за недостатка прав.")
@example(forbiddenExample)
model ForbiddenError {
    ...ProblemResponse<403>;

    @header("WWW-Authenticate")
    wwwAuthenticate?: string;
}
