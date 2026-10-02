namespace WorkersApi.Domain;

const coordinatesExample: Coordinates = #{
    x: 10,
    y: 10
};

@doc("Данные, которые пользователь передаёт для создания координат.")
@example(coordinatesExample)
model Coordinates {
    @doc("Координата x.")
    @minValueExclusive(-954)
    x: int32;

    @doc("Координата y.")
    @minValueExclusive(-392)
    y: int64;
}