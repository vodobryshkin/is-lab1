import "@typespec/http";

import "../model/error/AddressErrors.tsp";
import "../../common/ok/NoContentResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Ok;
using WorkersApi.Errors;
using WorkersApi.Addresses.Errors;

namespace WorkersApi.Addresses;

const deleteAddressSuccessOperationExample = #{
    parameters: #{ csrf_token: WorkersApi.Security.csrfTokenExample, id: 42 },
    returnType: #{ statusCode: 204 }
};

@delete
@route("/{id}")
@summary("Удалить адрес.")
@doc("Удаляет адрес, если он не используется организациями.")
@returnsDoc("Адрес удалён.")
@opExample(deleteAddressSuccessOperationExample)
@opExample(addressInvalidIdOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(forbiddenOperationExample)
@opExample(addressNotFoundOperationExample)
@opExample(deleteAddressConflictOperationExample)
op deleteAddress(
    ...WorkersApi.Security.CsrfTokenHeader,
    @path
    @minValue(1)
    id: int64
):
    WorkersApi.Ok.NoContentResponse
    | ValidationError<400, ParameterValidationIssue>
    | UnauthorizedError
    | ForbiddenError
    | AddressNotFoundError
    | DeleteAddressConflictError;