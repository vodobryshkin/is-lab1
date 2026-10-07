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
    parameters: #{ id: 42, body: indexSalariesExample },
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
            type: "/problems/validation-failed",
            title: "Validation failed",
            status: 422,
            errors: #[
                #{
                    field: "coefficient",
                    title: "MUST_BE_POSITIVE",
                    description: "Coefficient must be greater than zero."
                }
            ]
        }
    }
};

const indexSalariesConflictOperationExample = #{
    returnType: salaryIndexationConflictErrorExample
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
@opExample(organizationMissingTokenOperationExample)
@opExample(organizationInvalidTokenOperationExample)
@opExample(organizationForbiddenOperationExample)
@opExample(organizationNotFoundOperationExample)
@opExample(indexSalariesConflictOperationExample)
@opExample(indexSalariesValidationOperationExample)
op indexOrganizationSalaries(
    @path
    @minValue(1)
    id: int32,
    @body body: IndexSalariesRequest
):
    WorkersApi.Ok.OkResponse<CountResponse>
    | ValidationError<400, ParameterValidationIssue>
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | OrganizationNotFoundError
    | SalaryIndexationConflictError
    | ValidationError<422, SpecialRequestValidationIssue>;