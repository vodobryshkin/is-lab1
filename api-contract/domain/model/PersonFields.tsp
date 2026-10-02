import "./LocationFields.tsp";
import "../enum/Country.tsp";
import "../enum/Color.tsp";

namespace WorkersApi.Domain;

const personExample: PersonFields = #{
    eye_color: Color.BROWN,
    hair_color: Color.BROWN,
    location: locationExample,
    birthday: utcDateTime.fromISO("2026-10-02T17:16:48Z"),
    weight: 85,
    nationality: Country.RUSSIA
};

@doc("Данные, которые пользователь передаёт для создания персональных данных работника.")
@example(personExample)
model PersonFields {
    @doc("Цвет глаз человека.")
    eye_color: Color | null;

    @doc("Цвет волос человека.")
    hair_color: Color;

    @doc("Локация человека.")
    location: LocationFields;

    @doc("Дата рождения человека.")
    birthday: utcDateTime;

    @doc("Вес человека.")
    @minValueExclusive(0)
    weight: float32;

    @doc("Национальность человека.")
    nationality: Country;
}