import "@typespec/openapi3";
import "./OrganizationFields.tsp";

using TypeSpec.OpenAPI;

namespace WorkersApi.Domain;

alias OrganizationRequestFields =
    OmitProperties<OrganizationFields, "official_address" | "postal_address">;

const organizationWithNewAddressesExample = #{
    ...organizationExample
};

const organizationWithExistingAddressesExample = #{
    ...organizationBaseExample,
    official_address_id: 10,
    postal_address_id: 20
};

const organizationWithNewOfficialAddressExample = #{
    ...organizationBaseExample,
    official_address: addressExample,
    postal_address_id: 20
};

const organizationWithNewPostalAddressExample = #{
    ...organizationBaseExample,
    official_address_id: 10,
    postal_address: addressExample
};

@doc("Данные организации. Для каждого адреса передаётся ID или новый адрес.")
@oneOf
@example(organizationWithNewAddressesExample)
@example(organizationWithExistingAddressesExample)
@example(organizationWithNewOfficialAddressExample)
@example(organizationWithNewPostalAddressExample)
union OrganizationRequest {
    new_addresses: {
        ...OrganizationRequestFields;
        ...Record<never>;

        @doc("Новый официальный адрес.")
        official_address: AddressFields;

        @doc("Новый почтовый адрес.")
        postal_address: AddressFields;
    },

    existing_addresses: {
        ...OrganizationRequestFields;
        ...Record<never>;

        @doc("ID официального адреса.")
        @minValue(1)
        official_address_id: int32;

        @doc("ID почтового адреса.")
        @minValue(1)
        postal_address_id: int32;
    },

    new_official_address: {
        ...OrganizationRequestFields;
        ...Record<never>;

        @doc("Новый официальный адрес.")
        official_address: AddressFields;

        @doc("ID почтового адреса.")
        @minValue(1)
        postal_address_id: int32;
    },

    new_postal_address: {
        ...OrganizationRequestFields;
        ...Record<never>;

        @doc("ID официального адреса.")
        @minValue(1)
        official_address_id: int32;

        @doc("Новый почтовый адрес.")
        postal_address: AddressFields;
    },
}