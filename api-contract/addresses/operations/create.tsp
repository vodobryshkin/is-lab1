import "@typespec/http";

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
    parameters: #{ body: addressExample },
    returnType: #{
        statusCode: 201,
        contentType: "application/json",
        location: "/api/v1/addresses/42",
        body: idResponseExample
    }
};

const createAddressWithoutTownOperationExample = #{
    ...createAddressSuccessOperationExample,
    parameters: #{ body: #{ ...addressExample, town: null } }
};

@post
@route("")
@summary("Создать адрес.")
@doc("Создаёт адрес вместе с вложенной локацией.")
@returnsDoc("ID созданного адреса.")
@opExample(createAddressSuccessOperationExample)
@opExample(createAddressWithoutTownOperationExample)
@opExample(addressBadRequestOperationExample)
@opExample(addressMissingTokenOperationExample)
@opExample(addressInvalidTokenOperationExample)
@opExample(addressForbiddenOperationExample)
@opExample(addressValidationOperationExample)
op createAddress(
    @body body: AddressFields
):
    WorkersApi.Ok.CreatedResponse<IdResponse>
    | BadRequestError
    | UnauthorizedError
    | ForbiddenError
    | AddressValidationError;