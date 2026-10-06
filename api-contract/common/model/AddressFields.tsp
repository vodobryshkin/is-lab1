import "./LocationFields.tsp";

namespace WorkersApi.Domain;

const addressExample: AddressFields = #{
    street: "Ул. Пушкина",
    town: locationExample
};

@doc("Данные адреса.")
@example(addressExample)
model AddressFields {
    @doc("Название улицы.")
    @pattern("^.+$")
    @minLength(1)
    street: string;

    @doc("Локация города.")
    town: LocationFields | null;
}