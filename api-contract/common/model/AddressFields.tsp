import "./LocationRequest.tsp";

namespace WorkersApi.Domain;

const addressExample: AddressFields = #{
    street: "Ул. Пушкина",
    town: #{ ...locationExample }
};

const addressWithExistingLocationExample: AddressFields = #{
    ...addressExample,
    town: existingLocationExample
};

@doc("Данные адреса.")
@example(addressExample)
@example(addressWithExistingLocationExample)
model AddressFields {
    @doc("Название улицы.")
    @pattern("^.+$")
    @minLength(1)
    street: string;

    @doc("Новая локация, ID существующей или null.")
    town: LocationRequest | null;
}
