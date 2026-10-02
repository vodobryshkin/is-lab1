namespace WorkersApi.Domain;

const coordinatesExample: CoordinatesFields = #{
    x: 10,
    y: 10
};

@doc("Данные, которые пользователь передаёт для создания координат.")
@example(coordinatesExample)
model CoordinatesFields {
    @doc("Координата x.")
    @minValueExclusive(-954)
    x: int32;

    @doc("Координата y.")
    @minValueExclusive(-392)
    y: int64;
}