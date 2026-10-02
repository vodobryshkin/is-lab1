import "./Location.tsp";

namespace WorkersApi.Domain;

const addressExample: Address = #{
    street: "Ул. Пушкина",
    town: locationExample
};

@doc("Данные, которые пользователь передает при создании адреса.")
@example(addressExample)
model Address {
    @doc("Название улицы, к которой принадлежит адрес.")
    @pattern("^.+$")
    street: string;

    @doc("Локация города, к которому принадлежит адрес.")
    town: Location | null;
}