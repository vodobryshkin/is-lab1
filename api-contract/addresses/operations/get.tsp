import "@typespec/http";

import "../../common/model/ListParameters.tsp";
import "../../common/model/PageResponse.tsp";
import "../../common/error/ValidationError.tsp";

import "../model/response/GetSingleAddressResponse.tsp";
import "../model/error/AddressErrors.tsp";
import "../../common/ok/OkResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Domain;
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
@opExample(unauthorizedOperationExample)
@opExample(addressNotFoundOperationExample)
op getAddress(
    @path
    @minValue(1)
    id: int64
):
    WorkersApi.Ok.OkResponse<GetSingleAddressResponse>
    | BadRequestError
    | UnauthorizedError
    | NotFoundError;

const listAddressesPageExample: PageResponse<GetSingleAddressResponse> = #{
    items: #[getSingleAddressExample],
    page: 1,
    page_size: 20,
    total_items: 1
};

const listAddressesSuccessOperationExample = #{
    parameters: #{ page: 1, page_size: 20, sort_by: "id", sort_order: "asc" },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: listAddressesPageExample
    }
};

const listAddressesEmptyOperationExample = #{
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: #{ ...listAddressesPageExample, items: #[], total_items: 0 }
    }
};

const listAddressesPastEndOperationExample = #{
    parameters: #{ page: 2 },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: #{ ...listAddressesPageExample, page: 2, items: #[] }
    }
};

const listAddressesFilteredOperationExample = #{
    ...listAddressesSuccessOperationExample,
    parameters: #{
        street: "пушк",
        sort_by: "street",
        sort_order: "desc"
    }
};

@get
@route("")
@summary("Получить список адресов.")
@doc("Возвращает страницу адресов. Фильтры соединяются через AND; null не проходит непустой фильтр.")
@returnsDoc("Страница адресов с учётом фильтров.")
@opExample(listAddressesSuccessOperationExample)
@opExample(listAddressesEmptyOperationExample)
@opExample(listAddressesPastEndOperationExample)
@opExample(listAddressesFilteredOperationExample)
@opExample(listParameterValidationOperationExample)
@opExample(unauthorizedOperationExample)
op listAddresses(
    ...ListParameters,

    @doc("Поле сортировки. Строки сравниваются без учёта регистра.")
    @query
    sort_by?:
        "id" | "street" = "id",

    @doc("Название улицы.")
    @query
    street?: SubstringFilter,
):
    WorkersApi.Ok.OkResponse<PageResponse<GetSingleAddressResponse>>
    | BadRequestError
    | UnauthorizedError;
