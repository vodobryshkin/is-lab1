namespace WorkersApi.Domain;

@doc("Данные, которые пользователь передаёт для создания координат.")
model Coordinates {
    @minValueExclusive(-954)
    x: int32;

    @minValueExclusive(-392)
    y: int64;
}