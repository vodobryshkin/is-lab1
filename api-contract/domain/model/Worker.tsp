import "./Coordinates.tsp";
import "./Organization.tsp";
import "./Person.tsp";
import "../enum/Position.tsp";
import "../enum/Status.tsp";

namespace WorkersApi.Domain;

const workerExample: Worker = #{
    name: "Добрышкин Владимир Александрович",
    coordinates: coordinatesExample,
    organization_id: 1,
    salary: 100000,
    rating: 1000,
    start_date: utcDateTime.fromISO(""),
    position: Position.LEAD_DEVELOPER,
    status: Status.HIRED,
    person: personExample
};

@doc("Данные, которые пользователь передаёт для создания работника.")
@example(workerExample)
model Worker {
    @doc("Имя работника.")
    @pattern("^.+$")
    name: string;

    @doc("Координаты работника.")
    coordinates: Coordinates;

    @doc("Идентификатор организации, в которой работает работник.")
    organization_id: int64 | null;

    @doc("Зарплата работника.")
    @minValueExclusive(0)
    salary: float64;

    @doc("Рейтинг работника.")
    @minValueExclusive(0)
    rating: int32;

    @doc("Дата начала работы работником.")
    start_date: utcDateTime;

    @doc("Должность, которую занимает работник.")
    position: Position | null;

    @doc("Статус работника.")
    status: Status | null;

    @doc("Личные данные работника")
    person: Person | null;
}