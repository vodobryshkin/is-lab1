import "./Location.tsp";

namespace WorkersApi.Domain;

@doc("Данные, которые пользователь передает при создании адреса.")
model Address {
    street: string;

    town: Location;
}