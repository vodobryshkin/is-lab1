namespace WorkersApi.Domain;

@doc("Данные, которые пользователь передает при создании локации.")
model Location {
    x: int32;

    y: int32;

    z: int64;
}