import "./LocationFields.tsp";

namespace WorkersApi.Domain;

const addressExample: AddressFields = #{
    street: "Ул. Пушкина",
    town: locationExample
};

@doc("Данные, которые пользователь передает при domainсоздании адреса.")
@example(addressExample)
model AddressFields {
    @doc("Название улицы, к которой принадлежит адрес.")
    @pattern("^.+$")
    street: string;

    @doc("Локация города, к которому принадлежит адрес.")
    town: LocationFields | null;
}