import "@typespec/http";

import "../../common/error/NotFoundError.tsp";

import "../../common/model/AddressFields.tsp";
import "../../common/model/IdResponse.tsp";
import "../../common/ok/CreatedResponse.tsp";
import "../model/error/AddressErrors.tsp";

using TypeSpec.Http;
using WorkersApi.Domain;
using WorkersApi.Ok;
using WorkersApi.Errors;
using WorkersApi.Addresses.Errors;

namespace WorkersApi.Addresses;

const createAddressSuccessOperationExample = #{
    parameters: #{ csrf_token: WorkersApi.Security.csrfTokenExample, body: addressExample },
    returnType: #{
        statusCode: 201,
        contentType: "application/json",
        location: "/api/v1/addresses/42",
        body: idResponseExample
    }
};

const createAddressWithoutTownOperationExample = #{
    ...createAddressSuccessOperationExample,
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        body: #{ ...addressExample, town: null }
    }
};

const createAddressWithExistingLocationOperationExample = #{
    ...createAddressSuccessOperationExample,
    parameters: #{
        csrf_token: WorkersApi.Security.csrfTokenExample,
        body: addressWithExistingLocationExample
    }
};

@post
@route("")
@summary("Создать адрес.")
@doc("Создаёт адрес и привязывает новую или существующую локацию.")
@returnsDoc("ID созданного адреса.")
@opExample(locationNotFoundOperationExample)
@opExample(createAddressWithExistingLocationOperationExample)
@opExample(createAddressSuccessOperationExample)
@opExample(createAddressWithoutTownOperationExample)
@opExample(addressBadRequestOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(forbiddenOperationExample)
@opExample(addressValidationOperationExample)
op createAddress(
    ...WorkersApi.Security.CsrfTokenHeader,
    @body body: AddressFields
):
    WorkersApi.Ok.CreatedResponse<IdResponse>
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | NotFoundError
    | AddressValidationError;
