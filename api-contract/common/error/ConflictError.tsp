import "./ProblemResponse.tsp";

namespace WorkersApi.Errors;

const conflictExample: ConflictError = #{
    statusCode: 409,
    contentType: "application/json",
    body: #{
        type: "/problems/organization-has-workers",
        title: "Организацию невозможно удалить",
        status: 409,
        detail: "В организации есть работники. Перед удалением перенесите или удалите их.",
        instance: "/problem-occurrences/example-409"
    }
};

@error
@doc("Ошибка: конфликт с текущим состоянием ресурса.")
@example(conflictExample)
model ConflictError is ProblemResponse<409>;