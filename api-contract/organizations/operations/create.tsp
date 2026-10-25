import "@typespec/http";

import "../../common/error/NotFoundError.tsp";

import "../../common/model/OrganizationRequest.tsp";
import "../../common/model/IdResponse.tsp";
import "../../common/ok/CreatedResponse.tsp";
import "../model/error/OrganizationErrors.tsp";

using TypeSpec.Http;
using WorkersApi.Domain;
using WorkersApi.Ok;
using WorkersApi.Errors;
using WorkersApi.Organizations.Errors;

namespace WorkersApi.Organizations;

const createOrganizationSuccessOperationExample = #{
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        body: organizationWithNewAddressesExample
    },
    returnType: #{
        statusCode: 201,
        contentType: "application/json",
        body: idResponseExample
    }
};

const createOrganizationWithExistingAddressesOperationExample = #{
    ...createOrganizationSuccessOperationExample,
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        body: organizationWithExistingAddressesExample
    }
};

const createOrganizationWithNewOfficialAddressOperationExample = #{
    ...createOrganizationSuccessOperationExample,
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        body: organizationWithNewOfficialAddressExample
    }
};

const createOrganizationWithNewPostalAddressOperationExample = #{
    ...createOrganizationSuccessOperationExample,
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        body: organizationWithNewPostalAddressExample
    }
};

const createOrganizationWithExistingLocationOperationExample = #{
    ...createOrganizationSuccessOperationExample,
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        body: organizationWithExistingLocationsExample
    }
};

@post
@route("")
@summary("Создать организацию.")
@doc("Создаёт организацию, привязывает существующие адреса и создаёт новые в одной транзакции.")
@returnsDoc("ID созданной организации.")
@opExample(locationNotFoundOperationExample)
@opExample(createOrganizationWithExistingLocationOperationExample)
@opExample(createOrganizationSuccessOperationExample)
@opExample(createOrganizationWithExistingAddressesOperationExample)
@opExample(createOrganizationWithNewOfficialAddressOperationExample)
@opExample(createOrganizationWithNewPostalAddressOperationExample)
@opExample(organizationBadRequestOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(forbiddenOperationExample)
@opExample(organizationAddressNotFoundOperationExample)
@opExample(organizationNameConflictOperationExample)
@opExample(organizationValidationOperationExample)
@opExample(organizationAddressOptionsValidationOperationExample)
op createOrganization(
    ...WorkersApi.Security.CsrfTokenHeader,
    @body body: OrganizationRequest
):
    WorkersApi.Ok.CreatedResponse<IdResponse>
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | NotFoundError<AddressNotFoundDetails | LocationNotFoundDetails>
    | OrganizationNameConflictError
    | OrganizationValidationError;
