import "@typespec/http";

import "./get.tsp";
import "../../common/enum/Status.tsp";
import "../../common/model/CountResponse.tsp";
import "../../common/ok/OkResponse.tsp";
import "../../common/error/ValidationError.tsp";

using TypeSpec.Http;
using WorkersApi.Domain;
using WorkersApi.Ok;
using WorkersApi.Errors;

namespace WorkersApi.Workers;

const countWorkersByStatusOperationExample = #{
    parameters: #{ status: Status.HIRED },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: countResponseExample
    }
};

const countWorkersWithoutStatusOperationExample = #{
    parameters: #{ status: "null" },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: countResponseExample
    }
};

const countWorkersByStatusGreaterThanOperationExample = #{
    parameters: #{ status: Status.RECOMMENDED_FOR_PROMOTION },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: countResponseExample
    }
};

const countWorkersAboveRegularOperationExample = #{
    parameters: #{ status: Status.REGULAR },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: #{ count: 0 }
    }
};

const workerStatusValidationOperationExample = #{
    returnType: #{
        statusCode: 400,
        contentType: "application/json",
        body: #{
            type: "/problems/validation-failed",
            title: "Invalid request parameters",
            status: 400,
            errors: #[
                #{
                    field: "status",
                    title: "INVALID_ENUM_VALUE",
                    description: "The status value is not supported by this operation."
                }
            ]
        }
    }
};

@get
@route("/count_by_status")
@useAuth(WorkersApi.Security.SessionCookieAuth)
@summary("Посчитать работников по статусу.")
@doc("Считает работников с указанным статусом. Для пустого статуса передайте status=null. При отсутствии совпадений возвращает 0.")
@returnsDoc("Количество работников.")
@opExample(countWorkersByStatusOperationExample)
@opExample(countWorkersWithoutStatusOperationExample)
@opExample(workerStatusValidationOperationExample)
@opExample(unauthorizedOperationExample)
op countWorkersByStatus(
    @query status: Status | "null"
):
    WorkersApi.Ok.OkResponse<CountResponse>
    | ValidationError<400, ParameterValidationIssue>
    | UnauthorizedError;

@get
@route("/count_by_status_greater_than")
@useAuth(WorkersApi.Security.SessionCookieAuth)
@summary("Посчитать работников со статусом выше заданного.")
@doc("Порядок: HIRED < RECOMMENDED_FOR_PROMOTION < REGULAR. Работники со статусом null не учитываются.")
@returnsDoc("Количество работников.")
@opExample(countWorkersByStatusGreaterThanOperationExample)
@opExample(countWorkersAboveRegularOperationExample)
@opExample(workerStatusValidationOperationExample)
@opExample(unauthorizedOperationExample)
op countWorkersByStatusGreaterThan(
    @query status: Status
):
    WorkersApi.Ok.OkResponse<CountResponse>
    | ValidationError<400, ParameterValidationIssue>
    | UnauthorizedError;
