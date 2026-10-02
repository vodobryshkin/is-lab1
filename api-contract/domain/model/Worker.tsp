import "./Coordinates.tsp";
import "./Person.tsp";
import "../enum/Position.tsp";
import "../enum/Status.tsp";

namespace WorkersApi.Domain;

@doc("Данные, которые пользователь передаёт для создания работника.")
model Worker {
    name: string;

    coordinates: Coordinates;

    organization_id: int64;

    // organization: Organization

    salary: float64;

    rating: int32;

    start_date: utcDateTime;

    position: Position;

    status: Status;

    person: Person;
}