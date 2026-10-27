import "@typespec/http";

import "../model/request/IndexSalariesRequest.tsp";
import "../model/error/OrganizationErrors.tsp";
import "../../common/error/SpecialOperationErrors.tsp";
import "../../common/model/CountResponse.tsp";
import "../../common/ok/OkResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Ok;
using WorkersApi.Errors;
using WorkersApi.Organizations.Errors;

namespace WorkersApi.Organizations;

const indexSalariesSuccessOperationExample = #{
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        id: 42,
        body: indexSalariesExample
    },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: countResponseExample
    }
};

const indexSalariesEmptyOrganizationOperationExample = #{
    ...indexSalariesSuccessOperationExample,
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: #{ count: 0 }
    }
};

const indexSalariesValidationOperationExample = #{
    returnType: #{
        statusCode: 422,
        contentType: "application/json",
        body: #{
            errors: #[
                #{
                    field: "coefficient",
                    code: "MUST_BE_POSITIVE",
                    message: "coefficient must be greater than zero."
                }
            ]
        }
    }
};


@post
@route("/{id}/index_salaries")
@summary("Проиндексировать зарплаты организации.")
@doc("Умножает зарплаты на коэффициент в одной транзакции. Если результат нарушает ограничения, вся операция отменяется.")
@returnsDoc("Количество проиндексированных работников, включая 0 для организации без работников.")
@opExample(indexSalariesSuccessOperationExample)
@opExample(indexSalariesEmptyOrganizationOperationExample)
@opExample(organizationInvalidIdOperationExample)
@opExample(organizationBadRequestOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(forbiddenOperationExample)
@opExample(organizationNotFoundOperationExample)
@opExample(salaryIndexationConflictOperationExample)
@opExample(indexSalariesValidationOperationExample)
op indexOrganizationSalaries(
    ...WorkersApi.Security.CsrfTokenHeader,
    @path
    @minValue(1)
    id: int64,
    @body body: IndexSalariesRequest
):
    WorkersApi.Ok.OkResponse<CountResponse>
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | NotFoundError
    | ConflictError
    | ValidationError<SpecialRequestValidationIssue>;