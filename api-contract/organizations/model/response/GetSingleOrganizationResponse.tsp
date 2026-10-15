import "../../../common/model/OrganizationFields.tsp";
import "../../../addresses/model/response/GetSingleAddressResponse.tsp";

using WorkersApi.Domain;
using WorkersApi.Addresses;

namespace WorkersApi.Organizations;

const getSingleOrganizationExample: GetSingleOrganizationResponse = #{
    ...organizationExample,
    id: 42,
    official_address: #{ ...getSingleAddressExample, id: 10 },
    postal_address: #{ ...getSingleAddressExample, id: 20 }
};

@doc("Данные организации.")
@example(getSingleOrganizationExample)
model GetSingleOrganizationResponse {
    ...OmitProperties<OrganizationFields, "official_address" | "postal_address">;

    @doc("ID организации.")
    @minValue(1)
    id: int64;

    @doc("Официальный адрес.")
    official_address: GetSingleAddressResponse;

    @doc("Почтовый адрес.")
    postal_address: GetSingleAddressResponse;
}