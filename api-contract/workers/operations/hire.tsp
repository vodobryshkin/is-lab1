import "@typespec/http";

import "./get.tsp";
import "../model/request/HireWorkerRequest.tsp";
import "../../common/error/SpecialOperationErrors.tsp";
import "../../common/ok/NoContentResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Errors;
using WorkersApi.Workers.Errors;
using WorkersApi.Organizations.Errors;

namespace WorkersApi.Workers;

const hireWorkerSuccessOperationExample = #{
    parameters: #{ id: 42, body: hireWorkerExample },
    returnType: #{ statusCode: 204 }
};

const hireWorkerNotFoundOperationExample = #{
    parameters: #{ id: 999, body: hireWorkerExample },
    returnType: workerNotFoundErrorExample
};

const hireWorkerOrganizationNotFoundOperationExample = #{
    parameters: #{ id: 42, body: #{ organization_id: 999 } },
    returnType: organizationNotFoundErrorExample
};

const hireWorkerValidationOperationExample = #{
    returnType: #{
        statusCode: 422,
        contentType: "application/json",
        body: #{
            type: "/problems/validation-failed",
            title: "Validation failed",
            status: 422,
            errors: #[
                #{
                    field: "organization_id",
                    title: "MUST_BE_POSITIVE",
                    description: "Organization ID must be greater than zero."
                }
            ]
        }
    }
};

const hireWorkerForbiddenOperationExample = #{
    returnType: #{
        statusCode: 403,
        contentType: "application/json",
        body: #{
            type: "/problems/insufficient-permissions",
            title: "Insufficient permissions",
            status: 403,
            detail: "Hiring this worker is not allowed."
        }
    }
};

@post
@route("/{id}/hire")
@useAuth(BearerAuth)
@summary("Принять работника в организацию.")
@doc("Назначает организацию и статус HIRED в одной транзакции. Заменяет прежнюю привязку, если она была.")
@returnsDoc("Работник принят в организацию.")
@opExample(hireWorkerSuccessOperationExample)
@opExample(getWorkerInvalidIdOperationExample)
@opExample(organizationBadRequestOperationExample)
@opExample(getWorkerMissingTokenOperationExample)
@opExample(getWorkerInvalidTokenOperationExample)
@opExample(hireWorkerForbiddenOperationExample)
@opExample(hireWorkerNotFoundOperationExample)
@opExample(hireWorkerOrganizationNotFoundOperationExample)
@opExample(hireWorkerValidationOperationExample)
op hireWorker(
    @path
    @minValue(1)
    id: int32,
    @body body: HireWorkerRequest
):
    WorkersApi.Ok.NoContentResponse
    | InvalidWorkerIdError
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | HireWorkerNotFoundError
    | ValidationError<422, SpecialRequestValidationIssue>;