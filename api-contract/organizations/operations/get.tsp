import "@typespec/http";

import "../../common/model/ListParameters.tsp";
import "../../common/model/PageResponse.tsp";
import "../../common/error/ValidationError.tsp";

import "../model/response/GetSingleOrganizationResponse.tsp";
import "../model/error/OrganizationErrors.tsp";
import "../../common/ok/OkResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Domain;
using WorkersApi.Ok;
using WorkersApi.Errors;
using WorkersApi.Organizations.Errors;

namespace WorkersApi.Organizations;

const getOrganizationSuccessOperationExample = #{
    parameters: #{ id: 42 },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: getSingleOrganizationExample
    }
};

@get
@route("/{id}")
@summary("Получить организацию.")
@doc("Возвращает организацию и её вложенные адреса.")
@returnsDoc("Данные организации.")
@opExample(getOrganizationSuccessOperationExample)
@opExample(organizationInvalidIdOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(organizationNotFoundOperationExample)
op getOrganization(
    @path
    @minValue(1)
    id: int64
):
    WorkersApi.Ok.OkResponse<GetSingleOrganizationResponse>
    | BadRequestError
    | UnauthorizedError
    | NotFoundError;

const listOrganizationsPageExample: PageResponse<GetSingleOrganizationResponse> = #{
    items: #[getSingleOrganizationExample],
    page: 1,
    page_size: 20,
    total_items: 1
};

const listOrganizationsSuccessOperationExample = #{
    parameters: #{ page: 1, page_size: 20, sort_by: "id", sort_order: "asc" },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: listOrganizationsPageExample
    }
};

const listOrganizationsEmptyOperationExample = #{
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: #{ ...listOrganizationsPageExample, items: #[], total_items: 0 }
    }
};

const listOrganizationsPastEndOperationExample = #{
    parameters: #{ page: 2 },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: #{ ...listOrganizationsPageExample, page: 2, items: #[] }
    }
};

const listOrganizationsFilteredOperationExample = #{
    ...listOrganizationsSuccessOperationExample,
    parameters: #{
        full_name: "магистр",
        official_address_street: "пушк",
        sort_by: "full_name",
        sort_order: "desc"
    }
};

@get
@route("")
@summary("Получить список организаций.")
@doc("Возвращает страницу организаций. Фильтры соединяются через AND; null не проходит непустой фильтр.")
@returnsDoc("Страница организаций с учётом фильтров.")
@opExample(listOrganizationsSuccessOperationExample)
@opExample(listOrganizationsEmptyOperationExample)
@opExample(listOrganizationsPastEndOperationExample)
@opExample(listOrganizationsFilteredOperationExample)
@opExample(listParameterValidationOperationExample)
@opExample(unauthorizedOperationExample)
op listOrganizations(
    ...ListParameters,

    @doc("Поле сортировки. Строки сравниваются без учёта регистра.")
    @query
    sort_by?:
        "id" | "full_name" | "annual_turnover" | "employees_count" | "rating"
        | "type" | "official_address_id" | "postal_address_id"
        | "official_address_street" | "postal_address_street" = "id",

    @doc("Название организации.")
    @query
    full_name?: SubstringFilter,

    @doc("Тип организации.")
    @query
    type?: SubstringFilter,

    @doc("Улица официального адреса.")
    @query
    official_address_street?: SubstringFilter,

    @doc("Улица почтового адреса.")
    @query
    postal_address_street?: SubstringFilter,
):
    WorkersApi.Ok.OkResponse<PageResponse<GetSingleOrganizationResponse>>
    | BadRequestError
    | UnauthorizedError;
