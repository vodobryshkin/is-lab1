import "@typespec/http";

import "../model/error/AddressErrors.tsp";
import "../../common/ok/NoContentResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Ok;
using WorkersApi.Errors;
using WorkersApi.Addresses.Errors;

namespace WorkersApi.Addresses;

const deleteAddressSuccessOperationExample = #{
    parameters: #{ id: 42 },
    returnType: #{ statusCode: 204 }
};

@delete
@route("/{id}")
@summary("Удалить адрес.")
@doc("Удаляет адрес, если он не используется организациями.")
@returnsDoc("Адрес удалён.")
@opExample(deleteAddressSuccessOperationExample)
@opExample(addressInvalidIdOperationExample)
@opExample(addressMissingTokenOperationExample)
@opExample(addressInvalidTokenOperationExample)
@opExample(addressForbiddenOperationExample)
@opExample(addressNotFoundOperationExample)
@opExample(deleteAddressConflictOperationExample)
op deleteAddress(
    @path
    @minValue(1)
    id: int32
):
    WorkersApi.Ok.NoContentResponse
    | ValidationError<400, ParameterValidationIssue>
    | UnauthorizedError
    | ForbiddenError
    | AddressNotFoundError
    | DeleteAddressConflictError;