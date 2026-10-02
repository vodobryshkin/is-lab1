import "./Location.tsp";
import "../enum/Country.tsp";
import "../enum/Color.tsp";

namespace WorkersApi.Domain;

@doc("Данные, которые пользователь передаёт для создания персональных данных работника.")
model Person {
    eye_color: Color | null;

    hair_color: Color;

    location: Location;

    birthday: utcDateTime;

    @minValueExclusive(0)
    weight: float32;

    nationality: Country;
}