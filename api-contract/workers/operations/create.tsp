import "@typespec/http";

import "../model/request/CreateWorkerRequest.tsp";
import "../model/response/CreateWorkerResponse.tsp";

import "../model/error/CreateWorkerValidationError.tsp";
import "../model/error/CreateWorkerOrganizationNotFoundError.tsp";
import "../model/error/CreateWorkerOrganizationNameConflictError.tsp";

import "../../common/error/BadRequestError.tsp";
import "../../common/error/UnauthorizedError.tsp";
import "../../common/error/ForbiddenError.tsp";

using TypeSpec.Http;
using WorkersApi.Errors;
using WorkersApi.Workers.Errors;

namespace WorkersApi.Workers;

const createWorkerWithNewOrganizationOperationExample = #{
    parameters: #{ body: createWorkerWithNewOrganizationExample },
    returnType: createWorkerCreatedExample
};

const createWorkerWithExistingOrganizationOperationExample = #{
    parameters: #{ body: createWorkerWithExistingOrganizationExample },
    returnType: createWorkerCreatedExample
};

const createWorkerWithoutOrganizationOperationExample = #{
    parameters: #{ body: createWorkerWithoutOrganizationExample },
    returnType: #{
        ...createWorkerCreatedExample,
        body: #{ ...createdWorkerExample, organization_id: null }
    }
};

const createWorkerForbiddenExample: ForbiddenError = #{
    statusCode: 403,
    contentType: "application/json",
    body: #{
        type: "/problems/insufficient-permissions",
        title: "Insufficient permissions",
        status: 403,
        detail: "Creating workers is not allowed."
    }
};

const createWorkerBadRequestOperationExample = #{
    returnType: badRequestExample
};

const createWorkerUnauthorizedOperationExample = #{
    returnType: unauthorizedExample
};

const createWorkerForbiddenOperationExample = #{
    returnType: createWorkerForbiddenExample
};

const createWorkerOrganizationNotFoundOperationExample = #{
    returnType: createWorkerOrganizationNotFoundErrorExample
};

const createWorkerOrganizationNameConflictOperationExample = #{
    returnType: createWorkerOrganizationNameConflictErrorExample
};

const createWorkerValidationOperationExample = #{
    returnType: createWorkerValidationExample
};

@post
@route("")
@useAuth(BearerAuth)
@summary("Создать работника.")
@doc("Создаёт работника и новые вложенные объекты в одной транзакции.")
@opExample(createWorkerWithNewOrganizationOperationExample)
@opExample(createWorkerWithExistingOrganizationOperationExample)
@opExample(createWorkerWithoutOrganizationOperationExample)
@opExample(createWorkerBadRequestOperationExample)
@opExample(createWorkerUnauthorizedOperationExample)
@opExample(createWorkerForbiddenOperationExample)
@opExample(createWorkerOrganizationNotFoundOperationExample)
@opExample(createWorkerOrganizationNameConflictOperationExample)
@opExample(createWorkerValidationOperationExample)
op createWorker(
    @body body: CreateWorkerRequest
):
    WorkersApi.Ok.CreatedResponse<CreateWorkerResponse>
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | CreateWorkerOrganizationNotFoundError
    | CreateWorkerOrganizationNameConflictError
    | CreateWorkerValidationError;