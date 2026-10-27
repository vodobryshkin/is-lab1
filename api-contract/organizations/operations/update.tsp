import "@typespec/http";

import "../../common/error/NotFoundError.tsp";

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
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        id: 42,
        body: updateOrganizationExample
    },
    returnType: #{ statusCode: 204 }
};

const updateOrganizationWithNewAddressesOperationExample = #{
    ...updateOrganizationSuccessOperationExample,
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        id: 42,
        body: organizationWithNewAddressesExample
    }
};

const updateOrganizationWithNewOfficialAddressOperationExample = #{
    ...updateOrganizationSuccessOperationExample,
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        id: 42,
        body: organizationWithNewOfficialAddressExample
    }
};

const updateOrganizationWithNewPostalAddressOperationExample = #{
    ...updateOrganizationSuccessOperationExample,
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        id: 42,
        body: organizationWithNewPostalAddressExample
    }
};

const updateOrganizationWithExistingLocationOperationExample = #{
    ...updateOrganizationSuccessOperationExample,
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        id: 42,
        body: organizationWithExistingLocationsExample
    }
};

@put
@route("/{id}")
@summary("Обновить организацию.")
@doc("Заменяет поля организации и привязки адресов в одной транзакции.")
@returnsDoc("Организация обновлена.")
@opExample(locationNotFoundOperationExample)
@opExample(updateOrganizationWithExistingLocationOperationExample)
@opExample(updateOrganizationSuccessOperationExample)
@opExample(updateOrganizationWithNewAddressesOperationExample)
@opExample(updateOrganizationWithNewOfficialAddressOperationExample)
@opExample(updateOrganizationWithNewPostalAddressOperationExample)
@opExample(organizationInvalidIdOperationExample)
@opExample(organizationBadRequestOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(forbiddenOperationExample)
@opExample(organizationNotFoundOperationExample)
@opExample(organizationAddressNotFoundOperationExample)
@opExample(organizationNameConflictOperationExample)
@opExample(organizationValidationOperationExample)
@opExample(organizationAddressOptionsValidationOperationExample)
op updateOrganization(
    ...WorkersApi.Security.CsrfTokenHeader,
    @path
    @minValue(1)
    id: int64,
    @body body: OrganizationRequest
):
    WorkersApi.Ok.NoContentResponse
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | NotFoundError
    | ConflictError
    | OrganizationValidationError;
