import "../../../common/model/AddressFields.tsp";
import "../../../locations/model/response/GetSingleLocationResponse.tsp";

using WorkersApi.Domain;
using WorkersApi.Locations;

namespace WorkersApi.Addresses;

const getSingleAddressExample: GetSingleAddressResponse = #{
    ...addressExample,
    id: 42,
    town: getSingleLocationExample
};

@doc("Данные адреса.")
@example(getSingleAddressExample)
model GetSingleAddressResponse {
    ...OmitProperties<AddressFields, "town">;

    @doc("ID адреса.")
    @minValue(1)
    id: int64;

    @doc("Локация города или null.")
    town: GetSingleLocationResponse | null;
}
