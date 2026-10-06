import "./ProblemResponse.tsp";

namespace WorkersApi.Errors;

const notFoundExample: NotFoundError = #{
    statusCode: 404,
    contentType: "application/json",
    body: #{
        type: "/problems/worker-not-found",
        title: "Работник не найден",
        status: 404,
        detail: "Работник с идентификатором 42 не найден.",
        instance: "/problem-occurrences/example-404"
    }
};

@error
@doc("Ошибка: ресурс не найден либо его существование скрыто.")
@example(notFoundExample)
model NotFoundError is ProblemResponse<404>;