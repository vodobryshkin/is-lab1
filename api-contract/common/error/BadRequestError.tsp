import "./ProblemResponse.tsp";

namespace WorkersApi.Errors;

const badRequestExample: BadRequestError = #{
    statusCode: 400,
    contentType: "application/json",
    body: #{
        type: "/problems/malformed-json",
        title: "Некорректный JSON",
        status: 400,
        detail: "Тело запроса содержит незакрытую строку.",
        instance: "/problem-occurrences/example-400"
    }
};

@error
@doc("Ошибка: запрос невозможно обработать.")
@example(badRequestExample)
model BadRequestError is ProblemResponse<400>;