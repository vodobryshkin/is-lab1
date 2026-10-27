import "@typespec/http";

import "../../common/error/NotFoundError.tsp";
import "../../common/error/ConflictError.tsp";

import "../model/request/CreateWorkerRequest.tsp";
import "../model/response/CreateWorkerResponse.tsp";

import "../model/error/CreateWorkerValidationError.tsp";

import "../../common/error/BadRequestError.tsp";
import "../../common/error/UnauthorizedError.tsp";
import "../../common/error/ForbiddenError.tsp";

using TypeSpec.Http;
using WorkersApi.Domain;
using WorkersApi.Errors;
using WorkersApi.Workers.Errors;

namespace WorkersApi.Workers;

const createWorkerWithNewOrganizationOperationExample = #{
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        body: createWorkerWithNewOrganizationExample
    },
    returnType: createWorkerCreatedExample
};

const createWorkerWithExistingOrganizationOperationExample = #{
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        body: createWorkerWithExistingOrganizationExample
    },
    returnType: createWorkerCreatedExample
};

const createWorkerWithExistingAddressesOperationExample = #{
    ...createWorkerWithNewOrganizationOperationExample,
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        body: createWorkerWithExistingAddressesExample
    }
};

const createWorkerWithNewOfficialAddressOperationExample = #{
    ...createWorkerWithNewOrganizationOperationExample,
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        body: createWorkerWithNewOfficialAddressExample
    }
};

const createWorkerWithNewPostalAddressOperationExample = #{
    ...createWorkerWithNewOrganizationOperationExample,
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        body: createWorkerWithNewPostalAddressExample
    }
};

const createWorkerWithoutOrganizationOperationExample = #{
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        body: createWorkerWithoutOrganizationExample
    },
    returnType: #{
        ...createWorkerCreatedExample,
        body: #{ ...createdWorkerExample, organization_id: null }
    }
};

const createWorkerBadRequestOperationExample = #{
    returnType: badRequestExample
};

const createWorkerOrganizationNotFoundOperationExample = #{
    returnType: organizationNotFoundExample
};

const createWorkerOrganizationNameConflictOperationExample = #{
    returnType: organizationNameConflictExample
};

const createWorkerValidationOperationExample = #{
    returnType: createWorkerValidationExample
};

const createWorkerAddressNotFoundOperationExample = #{
    returnType: addressNotFoundExample
};

const createWorkerWithExistingObjectsOperationExample = #{
    ...createWorkerWithoutOrganizationOperationExample,
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        body: #{
            ...createWorkerWithoutOrganizationExample,
            coordinates: existingCoordinatesExample,
            person: existingPersonExample
        }
    }
};

const createWorkerWithExistingPersonLocationOperationExample = #{
    ...createWorkerWithoutOrganizationOperationExample,
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        body: #{
            ...createWorkerWithoutOrganizationExample,
            person: personWithExistingLocationExample
        }
    }
};

@post
@route("")
@useAuth(WorkersApi.Security.SessionCookieAuth)
@summary("Создать работника.")
@doc("Создаёт работника и новые вложенные объекты в одной транзакции.")
@opExample(locationNotFoundOperationExample)
@opExample(coordinatesNotFoundOperationExample)
@opExample(personNotFoundOperationExample)
@opExample(personAlreadyAssignedOperationExample)
@opExample(createWorkerWithExistingObjectsOperationExample)
@opExample(createWorkerWithExistingPersonLocationOperationExample)
@opExample(createWorkerReferenceValidationOperationExample)
@opExample(createWorkerWithNewOrganizationOperationExample)
@opExample(createWorkerWithExistingAddressesOperationExample)
@opExample(createWorkerWithNewOfficialAddressOperationExample)
@opExample(createWorkerWithNewPostalAddressOperationExample)
@opExample(createWorkerWithExistingOrganizationOperationExample)
@opExample(createWorkerWithoutOrganizationOperationExample)
@opExample(createWorkerBadRequestOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(forbiddenOperationExample)
@opExample(createWorkerOrganizationNotFoundOperationExample)
@opExample(createWorkerAddressNotFoundOperationExample)
@opExample(createWorkerOrganizationNameConflictOperationExample)
@opExample(createWorkerValidationOperationExample)
op createWorker(
    ...WorkersApi.Security.CsrfTokenHeader,
    @body body: CreateWorkerRequest
):
    WorkersApi.Ok.CreatedResponse<CreateWorkerResponse>
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | NotFoundError
    | ConflictError
    | CreateWorkerValidationError;
