import "./ProblemResponse.tsp";

namespace WorkersApi.Errors;

const badRequestExample: BadRequestError = #{
    statusCode: 400,
    contentType: "application/json",
    body: #{
        type: "/problems/malformed-json",
        title: "Malformed JSON",
        status: 400,
        detail: "The request body contains invalid JSON.",
        instance: "/problem-occurrences/example-400"
    }
};

const badRequestOperationExample = #{
    returnType: badRequestExample
};

@error
@doc("Ошибка: запрос невозможно обработать.")
@example(badRequestExample)
model BadRequestError is ProblemResponse<400>;
