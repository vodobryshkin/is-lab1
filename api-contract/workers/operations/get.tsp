import "../model/response/GetSingleWorkerResponse.tsp";
import "../../common/error/BadRequestError.tsp";
import "../../common/error/UnauthorizedError.tsp";
import "../../common/error/ForbiddenError.tsp";
import "../../common/error/NotFoundError.tsp";
import "../../common/ok/OkResponse.tsp";


using TypeSpec.Http;
using WorkersApi.Errors;

namespace WorkersApi.Workers;

@get
@route("/{id}")
@summary("Получить работника")
@doc("Возвращает данные работника по его идентификатору.")
op getWorker(
    @path
    @minValueExclusive(0)
    id: int64,
): WorkersApi.Ok.OkResponse<GetSingleWorkerResponse>
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | NotFoundError;