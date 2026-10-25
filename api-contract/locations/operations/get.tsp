import "@typespec/http";

import "../../common/model/ListParameters.tsp";
import "../../common/model/PageResponse.tsp";
import "../../common/error/ValidationError.tsp";

import "../model/response/GetSingleLocationResponse.tsp";
import "../model/error/LocationErrors.tsp";
import "../../common/ok/OkResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Domain;
using WorkersApi.Ok;
using WorkersApi.Errors;

namespace WorkersApi.Locations;

const getLocationSuccessOperationExample = #{
    parameters: #{ id: 7 },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: getSingleLocationExample
    }
};

@get
@route("/{id}")
@summary("Получить локацию.")
@doc("Возвращает данные локации.")
@returnsDoc("Данные локации.")
@opExample(getLocationSuccessOperationExample)
@opExample(invalidIdOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(locationNotFoundOperationExample)
op getLocation(
    @path
    @minValue(1)
    id: int64
):
    WorkersApi.Ok.OkResponse<GetSingleLocationResponse>
    | ValidationError<400, ParameterValidationIssue>
    | UnauthorizedError
    | NotFoundError<LocationNotFoundDetails>;

const listLocationsPageExample: PageResponse<GetSingleLocationResponse> = #{
    items: #[getSingleLocationExample],
    page: 1,
    page_size: 20,
    total_items: 1
};

const listLocationsSuccessOperationExample = #{
    parameters: #{ page: 1, page_size: 20, sort_by: "id", sort_order: "asc" },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: listLocationsPageExample
    }
};

const listLocationsEmptyOperationExample = #{
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: #{ ...listLocationsPageExample, items: #[], total_items: 0 }
    }
};

const listLocationsPastEndOperationExample = #{
    parameters: #{ page: 2 },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: #{ ...listLocationsPageExample, page: 2, items: #[] }
    }
};

@get
@route("")
@summary("Получить список локаций.")
@doc("Возвращает страницу локаций.")
@returnsDoc("Страница локаций с учётом фильтров.")
@opExample(listLocationsSuccessOperationExample)
@opExample(listLocationsEmptyOperationExample)
@opExample(listLocationsPastEndOperationExample)
@opExample(listParameterValidationOperationExample)
@opExample(unauthorizedOperationExample)
op listLocations(
    ...ListParameters,

    @doc("Поле сортировки. Строки сравниваются без учёта регистра.")
    @query
    sort_by?:
        "id" | "x" | "y" | "z" = "id",
):
    WorkersApi.Ok.OkResponse<PageResponse<GetSingleLocationResponse>>
    | ValidationError<400, ParameterValidationIssue>
    | UnauthorizedError;
