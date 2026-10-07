import "../../../common/model/AddressFields.tsp";

using WorkersApi.Domain;

namespace WorkersApi.Addresses;

const getSingleAddressExample: GetSingleAddressResponse = #{
    ...addressExample,
    id: 42
};

@doc("Данные адреса.")
@example(getSingleAddressExample)
model GetSingleAddressResponse {
    ...AddressFields;

    @doc("ID адреса.")
    @minValue(1)
    id: int32;
}