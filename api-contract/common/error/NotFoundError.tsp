import "./ProblemResponse.tsp";

namespace WorkersApi.Errors;

const notFoundExample: NotFoundError = #{
    statusCode: 404,
    contentType: "application/json",
    body: #{
        type: "/problems/worker-not-found",
        title: "Worker not found",
        status: 404,
        detail: "Worker with id=42 does not exist.",
        instance: "/problem-occurrences/example-404"
    }
};

@error
@doc("Ошибка: ресурс не найден либо его существование скрыто.")
model NotFoundError<TBody = ProblemDetails<404>> is ProblemResponse<404, TBody>;

@doc("Работник не найден.")
model WorkerNotFoundDetails {
    ...OmitProperties<ProblemDetails<404>, "type">;
    type: "/problems/worker-not-found";

    @doc("ID работника.")
    @minValue(1)
    worker_id: int32;
}

@doc("Организация не найдена.")
model OrganizationNotFoundDetails {
    ...OmitProperties<ProblemDetails<404>, "type">;
    type: "/problems/organization-not-found";

    @doc("ID организации.")
    @minValue(1)
    organization_id: int64;
}

@doc("Адрес не найден.")
model AddressNotFoundDetails {
    ...OmitProperties<ProblemDetails<404>, "type">;
    type: "/problems/address-not-found";
}

@doc("Координаты не найдены.")
model CoordinatesNotFoundDetails {
    ...OmitProperties<ProblemDetails<404>, "type">;
    type: "/problems/coordinates-not-found";

    @doc("ID координат.")
    @minValue(1)
    coordinates_id: int64;
}

@doc("Локация не найдена.")
model LocationNotFoundDetails {
    ...OmitProperties<ProblemDetails<404>, "type">;
    type: "/problems/location-not-found";

    @doc("ID локации.")
    @minValue(1)
    location_id: int64;
}

@doc("Персона не найдена.")
model PersonNotFoundDetails {
    ...OmitProperties<ProblemDetails<404>, "type">;
    type: "/problems/person-not-found";

    @doc("ID персоны.")
    @minValue(1)
    person_id: int64;
}

const coordinatesNotFoundExample: NotFoundError<CoordinatesNotFoundDetails> = #{
    statusCode: 404,
    contentType: "application/json",
    body: #{
        type: "/problems/coordinates-not-found",
        title: "Coordinates not found",
        status: 404,
        detail: "Coordinates with id=999 do not exist.",
        coordinates_id: 999
    }
};

const locationNotFoundExample: NotFoundError<LocationNotFoundDetails> = #{
    statusCode: 404,
    contentType: "application/json",
    body: #{
        type: "/problems/location-not-found",
        title: "Location not found",
        status: 404,
        detail: "Location with id=999 does not exist.",
        location_id: 999
    }
};

const personNotFoundExample: NotFoundError<PersonNotFoundDetails> = #{
    statusCode: 404,
    contentType: "application/json",
    body: #{
        type: "/problems/person-not-found",
        title: "Person not found",
        status: 404,
        detail: "Person with id=999 does not exist.",
        person_id: 999
    }
};

const coordinatesNotFoundOperationExample = #{
    returnType: coordinatesNotFoundExample
};

const locationNotFoundOperationExample = #{
    returnType: locationNotFoundExample
};

const personNotFoundOperationExample = #{
    returnType: personNotFoundExample
};
