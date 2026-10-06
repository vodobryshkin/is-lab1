import "./CoordinatesFields.tsp";
import "./OrganizationFields.tsp";
import "./PersonFields.tsp";
import "../enum/Position.tsp";
import "../enum/Status.tsp";

namespace WorkersApi.Domain;

const workerExample: WorkerFields = #{
    name: "Добрышкин Владимир Александрович",
    coordinates: coordinatesExample,
    salary: 100000,
    rating: 1000,
    start_date: utcDateTime.fromISO(""),
    position: Position.LEAD_DEVELOPER,
    status: Status.HIRED,
    person: personExample
};

@doc("Поля работника без идентификатора, даты создания и организации.")
@example(workerExample)
model WorkerFields {
    @doc("Имя работника.")
    @minLength(1)
    @pattern("^.+$")
    name: string;

    @doc("Координаты работника.")
    coordinates: CoordinatesFields;

    @doc("Зарплата работника.")
    @minValueExclusive(0)
    salary: float64;

    @doc("Рейтинг работника.")
    @minValueExclusive(0)
    rating: int32;

    @doc("Дата начала работы.")
    start_date: utcDateTime;

    @doc("Должность работника.")
    position: Position | null;

    @doc("Статус работника.")
    status: Status | null;

    @doc("Личные данные работника.")
    person: PersonFields | null;
}