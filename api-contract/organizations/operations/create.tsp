import "@typespec/http";

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
    parameters: #{ body: organizationWithNewAddressesExample },
    returnType: #{
        statusCode: 201,
        contentType: "application/json",
        body: idResponseExample
    }
};

const createOrganizationWithExistingAddressesOperationExample = #{
    ...createOrganizationSuccessOperationExample,
    parameters: #{ body: organizationWithExistingAddressesExample }
};

const createOrganizationWithNewOfficialAddressOperationExample = #{
    ...createOrganizationSuccessOperationExample,
    parameters: #{ body: organizationWithNewOfficialAddressExample }
};

const createOrganizationWithNewPostalAddressOperationExample = #{
    ...createOrganizationSuccessOperationExample,
    parameters: #{ body: organizationWithNewPostalAddressExample }
};

@post
@route("")
@summary("Создать организацию.")
@doc("Создаёт организацию, привязывает существующие адреса и создаёт новые в одной транзакции.")
@returnsDoc("ID созданной организации.")
@opExample(createOrganizationSuccessOperationExample)
@opExample(createOrganizationWithExistingAddressesOperationExample)
@opExample(createOrganizationWithNewOfficialAddressOperationExample)
@opExample(createOrganizationWithNewPostalAddressOperationExample)
@opExample(organizationBadRequestOperationExample)
@opExample(organizationMissingTokenOperationExample)
@opExample(organizationInvalidTokenOperationExample)
@opExample(organizationForbiddenOperationExample)
@opExample(organizationAddressNotFoundOperationExample)
@opExample(organizationNameConflictOperationExample)
@opExample(organizationValidationOperationExample)
@opExample(organizationAddressOptionsValidationOperationExample)
op createOrganization(
    @body body: OrganizationRequest
):
    WorkersApi.Ok.CreatedResponse<IdResponse>
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | AddressNotFoundError
    | OrganizationNameConflictError
    | OrganizationValidationError;