import "./Coordinates.tsp";
import "./Organization.tsp";
import "./Person.tsp";
import "../enum/Position.tsp";
import "../enum/Status.tsp";

namespace WorkersApi.Domain;

@doc("Данные, которые пользователь передаёт для создания работника.")
model Worker {
    @pattern("^.+$")
    name: string;

    coordinates: Coordinates;

    organization_id: int64 | null;

    organization: Organization | null;

    @minValueExclusive(0)
    salary: float64;

    @minValueExclusive(0)
    rating: int32;

    start_date: utcDateTime;

    position: Position | null;

    status: Status | null;

    person: Person | null;
}