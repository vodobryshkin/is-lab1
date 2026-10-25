import "@typespec/http";

import "../../common/error/NotFoundError.tsp";

import "../../common/model/AddressFields.tsp";
import "../../common/ok/NoContentResponse.tsp";
import "../model/error/AddressErrors.tsp";

using TypeSpec.Http;
using WorkersApi.Domain;
using WorkersApi.Ok;
using WorkersApi.Errors;
using WorkersApi.Addresses.Errors;

namespace WorkersApi.Addresses;

const updateAddressSuccessOperationExample = #{
    parameters: #{ csrf_token: WorkersApi.Security.csrfTokenExample, id: 42, body: addressExample },
    returnType: #{ statusCode: 204 }
};

const updateAddressWithoutTownOperationExample = #{
    ...updateAddressSuccessOperationExample,
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        id: 42,
        body: #{ ...addressExample, town: null }
    }
};

const updateAddressWithExistingLocationOperationExample = #{
    ...updateAddressSuccessOperationExample,
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        id: 42,
        body: addressWithExistingLocationExample
    }
};

@put
@route("/{id}")
@summary("Обновить адрес.")
@doc("Заменяет поля адреса и привязку локации в одной транзакции.")
@returnsDoc("Адрес обновлён.")
@opExample(locationNotFoundOperationExample)
@opExample(updateAddressWithExistingLocationOperationExample)
@opExample(updateAddressSuccessOperationExample)
@opExample(updateAddressWithoutTownOperationExample)
@opExample(addressInvalidIdOperationExample)
@opExample(addressBadRequestOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(forbiddenOperationExample)
@opExample(addressNotFoundOperationExample)
@opExample(addressValidationOperationExample)
op updateAddress(
    ...WorkersApi.Security.CsrfTokenHeader,
    @path
    @minValue(1)
    id: int64,
    @body body: AddressFields
):
    WorkersApi.Ok.NoContentResponse
    | ValidationError<400, ParameterValidationIssue>
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | NotFoundError<AddressNotFoundDetails | LocationNotFoundDetails>
    | AddressValidationError;
