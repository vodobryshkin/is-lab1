import "./AddressValidationIssue.tsp";

namespace WorkersApi.Errors;

@doc("Нарушения полей организации и вложенных адресов.")
union OrganizationValidationIssue<path extends string> {
    official_address: {
        field: "${path}.official_address";
        title: "required" | "must_not_be_null" | "invalid_type";
        description: string;
    },

    annual_turnover: {
        field: "${path}.annual_turnover";
        title: "required" | "must_not_be_null" | "invalid_type"
            | "must_be_positive" | "out_of_range";
        description: string;
    },

    employees_count: {
        field: "${path}.employees_count";
        title: "required" | "must_not_be_null" | "invalid_type"
            | "must_be_positive" | "out_of_range";
        description: string;
    },

    full_name: {
        field: "${path}.full_name";
        title: "required" | "must_not_be_null" | "invalid_type"
            | "must_not_be_empty" | "must_match_pattern";
        description: string;
    },

    rating: {
        field: "${path}.rating";
        title: "required" | "must_not_be_null" | "invalid_type"
            | "must_be_positive" | "out_of_range";
        description: string;
    },

    type: {
        field: "${path}.type";
        title: "required" | "invalid_type" | "invalid_enum_value";
        description: string;
    },

    postal_address: {
        field: "${path}.postal_address";
        title: "required" | "must_not_be_null" | "invalid_type";
        description: string;
    },

    official_address_fields:
        AddressValidationIssue<"${path}.official_address">,

    postal_address_fields:
        AddressValidationIssue<"${path}.postal_address">,
}