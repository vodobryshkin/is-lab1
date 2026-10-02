namespace WorkersApi.Domain;

@doc("Данные, которые пользователь передаёт для создания координат.")
model Coordinates {
    x: int32;

    y: int64;
}