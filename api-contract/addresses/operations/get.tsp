import "@typespec/http";

import "../model/response/GetSingleAddressResponse.tsp";
import "../model/error/AddressErrors.tsp";
import "../../common/ok/OkResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Ok;
using WorkersApi.Errors;
using WorkersApi.Addresses.Errors;

namespace WorkersApi.Addresses;

const getAddressSuccessOperationExample = #{
    parameters: #{ id: 42 },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: getSingleAddressExample
    }
};

const getAddressWithoutTownOperationExample = #{
    ...getAddressSuccessOperationExample,
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: #{ ...getSingleAddressExample, town: null }
    }
};

@get
@route("/{id}")
@summary("Получить адрес.")
@doc("Возвращает адрес и вложенную локацию.")
@returnsDoc("Данные адреса.")
@opExample(getAddressSuccessOperationExample)
@opExample(getAddressWithoutTownOperationExample)
@opExample(addressInvalidIdOperationExample)
@opExample(addressMissingTokenOperationExample)
@opExample(addressInvalidTokenOperationExample)
@opExample(addressNotFoundOperationExample)
op getAddress(
    @path
    @minValue(1)
    id: int32
):
    WorkersApi.Ok.OkResponse<GetSingleAddressResponse>
    | ValidationError<400, ParameterValidationIssue>
    | UnauthorizedError
    | AddressNotFoundError;