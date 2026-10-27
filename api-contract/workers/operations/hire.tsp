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
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        id: 42,
        body: hireWorkerExample
    },
    returnType: #{ statusCode: 204 }
};

const hireWorkerNotFoundOperationExample = #{
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        id: 999,
        body: hireWorkerExample
    },
    returnType: workerNotFoundExample
};

const hireWorkerOrganizationNotFoundOperationExample = #{
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        id: 42,
        body: #{ organization_id: 999 }
    },
    returnType: organizationNotFoundExample
};

const hireWorkerValidationOperationExample = #{
    returnType: #{
        statusCode: 422,
        contentType: "application/json",
        body: #{
            errors: #[
                #{
                    field: "organization_id",
                    code: "MUST_BE_POSITIVE",
                    message: "organization_id must be greater than zero."
                }
            ]
        }
    }
};

@post
@route("/{id}/hire")
@useAuth(WorkersApi.Security.SessionCookieAuth)
@summary("Принять работника в организацию.")
@doc("Назначает организацию и статус HIRED в одной транзакции. Заменяет прежнюю привязку, если она была.")
@returnsDoc("Работник принят в организацию.")
@opExample(hireWorkerSuccessOperationExample)
@opExample(getWorkerInvalidIdOperationExample)
@opExample(organizationBadRequestOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(forbiddenOperationExample)
@opExample(hireWorkerNotFoundOperationExample)
@opExample(hireWorkerOrganizationNotFoundOperationExample)
@opExample(hireWorkerValidationOperationExample)
op hireWorker(
    ...WorkersApi.Security.CsrfTokenHeader,
    @path
    @minValue(1)
    id: int32,
    @body body: HireWorkerRequest
):
    WorkersApi.Ok.NoContentResponse
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | NotFoundError
    | ValidationError<SpecialRequestValidationIssue>;