import "@typespec/http";

import "../../common/model/OrganizationRequest.tsp";
import "../../common/ok/NoContentResponse.tsp";
import "../model/error/OrganizationErrors.tsp";

using TypeSpec.Http;
using WorkersApi.Domain;
using WorkersApi.Ok;
using WorkersApi.Errors;
using WorkersApi.Organizations.Errors;

namespace WorkersApi.Organizations;

const updateOrganizationExample: OrganizationRequest = #{
    ...organizationWithExistingAddressesExample,
    rating: 1200
};

const updateOrganizationSuccessOperationExample = #{
    parameters: #{ id: 42, body: updateOrganizationExample },
    returnType: #{ statusCode: 204 }
};

const updateOrganizationWithNewAddressesOperationExample = #{
    ...updateOrganizationSuccessOperationExample,
    parameters: #{ id: 42, body: organizationWithNewAddressesExample }
};

const updateOrganizationWithNewOfficialAddressOperationExample = #{
    ...updateOrganizationSuccessOperationExample,
    parameters: #{ id: 42, body: organizationWithNewOfficialAddressExample }
};

const updateOrganizationWithNewPostalAddressOperationExample = #{
    ...updateOrganizationSuccessOperationExample,
    parameters: #{ id: 42, body: organizationWithNewPostalAddressExample }
};

@put
@route("/{id}")
@summary("Обновить организацию.")
@doc("Заменяет поля организации и привязки адресов в одной транзакции.")
@returnsDoc("Организация обновлена.")
@opExample(updateOrganizationSuccessOperationExample)
@opExample(updateOrganizationWithNewAddressesOperationExample)
@opExample(updateOrganizationWithNewOfficialAddressOperationExample)
@opExample(updateOrganizationWithNewPostalAddressOperationExample)
@opExample(organizationInvalidIdOperationExample)
@opExample(organizationBadRequestOperationExample)
@opExample(organizationMissingTokenOperationExample)
@opExample(organizationInvalidTokenOperationExample)
@opExample(organizationForbiddenOperationExample)
@opExample(organizationNotFoundOperationExample)
@opExample(organizationAddressNotFoundOperationExample)
@opExample(organizationNameConflictOperationExample)
@opExample(organizationValidationOperationExample)
@opExample(organizationAddressOptionsValidationOperationExample)
op updateOrganization(
    @path
    @minValue(1)
    id: int32,
    @body body: OrganizationRequest
):
    WorkersApi.Ok.NoContentResponse
    | ValidationError<400, ParameterValidationIssue>
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | OrganizationNotFoundError
    | AddressNotFoundError
    | OrganizationNameConflictError
    | OrganizationValidationError;