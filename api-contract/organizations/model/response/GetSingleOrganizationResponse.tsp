import "../../../common/model/OrganizationFields.tsp";

using WorkersApi.Domain;

namespace WorkersApi.Organizations;

const getSingleOrganizationExample: GetSingleOrganizationResponse = #{
    ...organizationExample,
    id: 42,
    official_address_id: 10,
    postal_address_id: 20
};

@doc("Данные организации.")
@example(getSingleOrganizationExample)
model GetSingleOrganizationResponse {
    ...OrganizationFields;

    @doc("ID организации.")
    @minValue(1)
    id: int32;

    @doc("ID официального адреса.")
    @minValue(1)
    official_address_id: int32;

    @doc("ID почтового адреса.")
    @minValue(1)
    postal_address_id: int32;
}