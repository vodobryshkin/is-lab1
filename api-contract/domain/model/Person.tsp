import "./Location.tsp";
import "../enum/Country.tsp";
import "../enum/Color.tsp";

namespace WorkersApi.Domain;

@doc("Данные, которые пользователь передаёт для создания персональных данных работника.")
model Person {
    eye_color: Color;

    hair_color: Color;

    birthday: utcDateTime;

    weight: float32;

    location: Location;

    nationality: Country;
}