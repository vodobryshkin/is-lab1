import "@typespec/http";
import "./ProblemDetails.tsp";

using TypeSpec.Http;

namespace WorkersApi.Errors;

const unauthorizedExample: UnauthorizedError = #{
    statusCode: 401,
    wwwAuthenticate: "Bearer realm=\"Workers API\", error=\"invalid_token\"",
    contentType: "application/problem+json",
    body: #{
        type: "/problems/invalid-token",
        title: "Invalid token",
        status: 401,
        detail: "The bearer token is invalid or expired.",
        instance: "/problem-occurrences/example-401"
    }
};

const missingTokenExample: UnauthorizedError = #{
    ...unauthorizedExample,
    wwwAuthenticate: "Bearer realm=\"Workers API\"",
    body: #{
        type: "/problems/missing-token",
        title: "Authentication required",
        status: 401,
        detail: "A bearer token is required."
    }
};

const missingTokenOperationExample = #{
    returnType: missingTokenExample
};

const invalidTokenOperationExample = #{
    returnType: unauthorizedExample
};

@error
@doc("Ошибка: отсутствуют действительные данные аутентификации.")
@example(unauthorizedExample)
model UnauthorizedError {
    @statusCode
    statusCode: 401;

    @header("WWW-Authenticate")
    wwwAuthenticate: string;

    @header("Content-Type")
    contentType: "application/problem+json";

    @body
    body?: ProblemDetails<401>;
}
