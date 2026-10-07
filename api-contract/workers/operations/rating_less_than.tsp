import "@typespec/http";

import "./get.tsp";
import "../model/response/GetSingleWorkerResponse.tsp";
import "../../common/ok/OkResponse.tsp";
import "../../common/error/ValidationError.tsp";

using TypeSpec.Http;
using WorkersApi.Errors;

namespace WorkersApi.Workers;

const getWorkersByRatingOperationExample = #{
    parameters: #{ rating: 1001 },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: #[getSingleWorkerExample]
    }
};

const getWorkersByRatingEmptyOperationExample = #{
    parameters: #{ rating: 0 },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: #[]
    }
};

const workerRatingValidationOperationExample = #{
    returnType: #{
        statusCode: 400,
        contentType: "application/json",
        body: #{
            type: "/problems/validation-failed",
            title: "Invalid request parameters",
            status: 400,
            errors: #[
                #{
                    field: "rating",
                    title: "INVALID_TYPE",
                    description: "Rating must be an int32 value."
                }
            ]
        }
    }
};

@get
@route("/rating_less_than")
@useAuth(BearerAuth)
@summary("Получить работников с рейтингом ниже заданного.")
@doc("Возвращает всех работников с rating строго меньше порога. При отсутствии совпадений возвращает пустой массив.")
@returnsDoc("Массив работников.")
@opExample(getWorkersByRatingOperationExample)
@opExample(getWorkersByRatingEmptyOperationExample)
@opExample(workerRatingValidationOperationExample)
@opExample(getWorkerMissingTokenOperationExample)
@opExample(getWorkerInvalidTokenOperationExample)
op getWorkersByRatingLessThan(
    @query rating: int32
):
    WorkersApi.Ok.OkResponse<GetSingleWorkerResponse[]>
    | ValidationError<400, ParameterValidationIssue>
    | UnauthorizedError;