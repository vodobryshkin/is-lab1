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
            message: "Parameter rating must be an integer."
        }
    }
};

@get
@route("/rating_less_than")
@useAuth(WorkersApi.Security.SessionCookieAuth)
@summary("Получить работников с рейтингом ниже заданного.")
@doc("Возвращает всех работников с rating строго меньше порога. При отсутствии совпадений возвращает пустой массив.")
@returnsDoc("Массив работников.")
@opExample(getWorkersByRatingOperationExample)
@opExample(getWorkersByRatingEmptyOperationExample)
@opExample(workerRatingValidationOperationExample)
@opExample(unauthorizedOperationExample)
op getWorkersByRatingLessThan(
    @query rating: int32
):
    WorkersApi.Ok.OkResponse<GetSingleWorkerResponse[]>
    | BadRequestError
    | UnauthorizedError;