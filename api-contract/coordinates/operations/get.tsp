import "@typespec/http";

import "../../common/model/ListParameters.tsp";
import "../../common/model/PageResponse.tsp";
import "../../common/error/ValidationError.tsp";

import "../model/response/GetSingleCoordinatesResponse.tsp";
import "../model/error/CoordinatesErrors.tsp";
import "../../common/ok/OkResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Domain;
using WorkersApi.Ok;
using WorkersApi.Errors;

namespace WorkersApi.Coordinates;

const getCoordinatesSuccessOperationExample = #{
    parameters: #{ id: 5 },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: getSingleCoordinatesExample
    }
};

@get
@route("/{id}")
@summary("Получить координаты.")
@doc("Возвращает данные координат.")
@returnsDoc("Данные координат.")
@opExample(getCoordinatesSuccessOperationExample)
@opExample(invalidIdOperationExample)
@opExample(missingTokenOperationExample)
@opExample(invalidTokenOperationExample)
@opExample(coordinatesNotFoundOperationExample)
op getCoordinates(
    @path
    @minValue(1)
    id: int32
):
    WorkersApi.Ok.OkResponse<GetSingleCoordinatesResponse>
    | ValidationError<400, ParameterValidationIssue>
    | UnauthorizedError
    | NotFoundError<CoordinatesNotFoundDetails>;

const listCoordinatesPageExample: PageResponse<GetSingleCoordinatesResponse> = #{
    items: #[getSingleCoordinatesExample],
    page: 1,
    page_size: 20,
    total_items: 1
};

const listCoordinatesSuccessOperationExample = #{
    parameters: #{ page: 1, page_size: 20, sort_by: "id", sort_order: "asc" },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: listCoordinatesPageExample
    }
};

const listCoordinatesEmptyOperationExample = #{
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: #{ ...listCoordinatesPageExample, items: #[], total_items: 0 }
    }
};

const listCoordinatesPastEndOperationExample = #{
    parameters: #{ page: 2 },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: #{ ...listCoordinatesPageExample, page: 2, items: #[] }
    }
};

@get
@route("")
@summary("Получить список координат.")
@doc("Возвращает страницу координат.")
@returnsDoc("Страница координат с учётом фильтров.")
@opExample(listCoordinatesSuccessOperationExample)
@opExample(listCoordinatesEmptyOperationExample)
@opExample(listCoordinatesPastEndOperationExample)
@opExample(listParameterValidationOperationExample)
@opExample(missingTokenOperationExample)
@opExample(invalidTokenOperationExample)
op listCoordinates(
    ...ListParameters,

    @doc("Поле сортировки. Строки сравниваются без учёта регистра.")
    @query
    sort_by?:
        "id" | "x" | "y" = "id",
):
    WorkersApi.Ok.OkResponse<PageResponse<GetSingleCoordinatesResponse>>
    | ValidationError<400, ParameterValidationIssue>
    | UnauthorizedError;
