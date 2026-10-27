import "@typespec/http";

import "../../common/model/ListParameters.tsp";
import "../../common/model/PageResponse.tsp";
import "../../common/error/ValidationError.tsp";

import "../model/response/GetSingleWorkerResponse.tsp";

import "../../common/error/UnauthorizedError.tsp";
import "../../common/ok/OkResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Ok;
using WorkersApi.Domain;
using WorkersApi.Errors;
using WorkersApi.Workers.Errors;

namespace WorkersApi.Workers;

const getWorkerSuccessOperationExample = #{
    parameters: #{ id: 42 },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: getSingleWorkerExample
    }
};

const getWorkerWithoutOrganizationOperationExample = #{
    parameters: #{ id: 42 },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: getSingleWorkerWithoutOrganizationExample
    }
};

const getWorkerMaxIdOperationExample = #{
    parameters: #{ id: 2147483647 },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: #{ ...getSingleWorkerExample, id: 2147483647 }
    }
};

const getWorkerInvalidIdOperationExample = #{
    returnType: invalidIdExample
};

const getWorkerNotFoundOperationExample = #{
    parameters: #{ id: 999 },
    returnType: workerNotFoundExample
};

@get
@route("/{id}")
@useAuth(WorkersApi.Security.SessionCookieAuth)
@summary("Получить работника.")
@doc("Возвращает работника и его вложенные объекты.")
@opExample(getWorkerSuccessOperationExample)
@opExample(getWorkerMaxIdOperationExample)
@opExample(getWorkerWithoutOrganizationOperationExample)
@opExample(getWorkerInvalidIdOperationExample)
@opExample(unauthorizedOperationExample)
@opExample(getWorkerNotFoundOperationExample)
op getWorker(
    @path
    @minValue(1)
    id: int32
):
    WorkersApi.Ok.OkResponse<GetSingleWorkerResponse>
    | BadRequestError
    | UnauthorizedError
    | NotFoundError;

const listWorkersPageExample: PageResponse<GetSingleWorkerResponse> = #{
    items: #[getSingleWorkerExample],
    page: 1,
    page_size: 20,
    total_items: 1
};

const listWorkersSuccessOperationExample = #{
    parameters: #{ page: 1, page_size: 20, sort_by: "id", sort_order: "asc" },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: listWorkersPageExample
    }
};

const listWorkersEmptyOperationExample = #{
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: #{ ...listWorkersPageExample, items: #[], total_items: 0 }
    }
};

const listWorkersPastEndOperationExample = #{
    parameters: #{ page: 2 },
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        body: #{ ...listWorkersPageExample, page: 2, items: #[] }
    }
};

const listWorkersFilteredOperationExample = #{
    ...listWorkersSuccessOperationExample,
    parameters: #{
        name: "добрыш",
        organization_full_name: "магистр",
        person_hair_color: "brown",
        sort_by: "name",
        sort_order: "desc"
    }
};

@get
@route("")
@summary("Получить список работников.")
@doc("Возвращает страницу работников. Фильтры соединяются через AND; null не проходит непустой фильтр.")
@returnsDoc("Страница работников с учётом фильтров.")
@opExample(listWorkersSuccessOperationExample)
@opExample(listWorkersEmptyOperationExample)
@opExample(listWorkersPastEndOperationExample)
@opExample(listWorkersFilteredOperationExample)
@opExample(listParameterValidationOperationExample)
@opExample(unauthorizedOperationExample)
op listWorkers(
    ...ListParameters,

    @doc("Поле сортировки. Строки сравниваются без учёта регистра.")
    @query
    sort_by?:
        "id" | "name" | "salary" | "rating" | "start_date" | "creation_date"
        | "position" | "status" | "organization_full_name" | "organization_type"
        | "organization_official_address_street"
        | "organization_postal_address_street" | "person_eye_color"
        | "person_hair_color" | "person_nationality" = "id",

    @doc("Имя работника.")
    @query
    name?: SubstringFilter,

    @doc("Должность работника.")
    @query
    position?: SubstringFilter,

    @doc("Статус работника.")
    @query
    status?: SubstringFilter,

    @doc("Название организации.")
    @query
    organization_full_name?: SubstringFilter,

    @doc("Тип организации.")
    @query
    organization_type?: SubstringFilter,

    @doc("Улица официального адреса организации.")
    @query
    organization_official_address_street?: SubstringFilter,

    @doc("Улица почтового адреса организации.")
    @query
    organization_postal_address_street?: SubstringFilter,

    @doc("Цвет глаз персоны.")
    @query
    person_eye_color?: SubstringFilter,

    @doc("Цвет волос персоны.")
    @query
    person_hair_color?: SubstringFilter,

    @doc("Национальность персоны.")
    @query
    person_nationality?: SubstringFilter,
):
    WorkersApi.Ok.OkResponse<PageResponse<GetSingleWorkerResponse>>
    | BadRequestError
    | UnauthorizedError;
