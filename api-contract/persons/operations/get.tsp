import "@typespec/http";

import "../../common/model/ListParameters.tsp";
import "../../common/model/PageResponse.tsp";
import "../../common/error/ValidationError.tsp";

import "../model/response/GetSinglePersonResponse.tsp";
import "../model/error/PersonErrors.tsp";
import "../../common/ok/OkResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Domain;
using WorkersApi.Ok;
using WorkersApi.Errors;

namespace WorkersApi.Persons;

const getPersonSuccessOperationExample = #{
    parameters: #{ id: 10 },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: getSinglePersonExample
    }
};

@get
@route("/{id}")
@summary("Получить персону.")
@doc("Возвращает персону и её локацию.")
@returnsDoc("Данные персоны.")
@opExample(getPersonSuccessOperationExample)
@opExample(invalidIdOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(personNotFoundOperationExample)
op getPerson(
    @path
    @minValue(1)
    id: int64
):
    WorkersApi.Ok.OkResponse<GetSinglePersonResponse>
    | ValidationError<400, ParameterValidationIssue>
    | UnauthorizedError
    | NotFoundError<PersonNotFoundDetails>;

const listPersonsPageExample: PageResponse<GetSinglePersonResponse> = #{
    items: #[getSinglePersonExample],
    page: 1,
    page_size: 20,
    total_items: 1
};

const listPersonsSuccessOperationExample = #{
    parameters: #{ page: 1, page_size: 20, sort_by: "id", sort_order: "asc" },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: listPersonsPageExample
    }
};

const listPersonsEmptyOperationExample = #{
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: #{ ...listPersonsPageExample, items: #[], total_items: 0 }
    }
};

const listPersonsPastEndOperationExample = #{
    parameters: #{ page: 2 },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: #{ ...listPersonsPageExample, page: 2, items: #[] }
    }
};

const listPersonsFilteredOperationExample = #{
    ...listPersonsSuccessOperationExample,
    parameters: #{
        hair_color: "brown",
        nationality: "russ",
        sort_by: "nationality",
        sort_order: "desc"
    }
};

@get
@route("")
@summary("Получить список персон.")
@doc("Возвращает страницу персон. Фильтры соединяются через AND; null не проходит непустой фильтр.")
@returnsDoc("Страница персон с учётом фильтров.")
@opExample(listPersonsSuccessOperationExample)
@opExample(listPersonsEmptyOperationExample)
@opExample(listPersonsPastEndOperationExample)
@opExample(listPersonsFilteredOperationExample)
@opExample(listParameterValidationOperationExample)
@opExample(unauthorizedOperationExample)
op listPersons(
    ...ListParameters,

    @doc("Поле сортировки. Строки сравниваются без учёта регистра.")
    @query
    sort_by?:
        "id" | "eye_color" | "hair_color" | "nationality" | "birthday"
        | "weight" = "id",

    @doc("Цвет глаз.")
    @query
    eye_color?: SubstringFilter,

    @doc("Цвет волос.")
    @query
    hair_color?: SubstringFilter,

    @doc("Национальность.")
    @query
    nationality?: SubstringFilter,
):
    WorkersApi.Ok.OkResponse<PageResponse<GetSinglePersonResponse>>
    | ValidationError<400, ParameterValidationIssue>
    | UnauthorizedError;
