import "./AddressValidationIssue.tsp";

namespace WorkersApi.Errors;

@doc("Нарушения полей организации и вложенных адресов.")
union OrganizationValidationIssue<path extends string> {
    official_address: {
        field: "${path}.official_address";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE";
        description: string;
    },

    annual_turnover: {
        field: "${path}.annual_turnover";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        description: string;
    },

    employees_count: {
        field: "${path}.employees_count";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        description: string;
    },

    full_name: {
        field: "${path}.full_name";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_NOT_BE_EMPTY" | "MUST_MATCH_PATTERN";
        description: string;
    },

    rating: {
        field: "${path}.rating";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        description: string;
    },

    type: {
        field: "${path}.type";
        title: "REQUIRED" | "INVALID_TYPE" | "INVALID_ENUM_VALUE";
        description: string;
    },

    postal_address: {
        field: "${path}.postal_address";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE";
        description: string;
    },

    official_address_fields:
        AddressValidationIssue<"${path}.official_address">,

    postal_address_fields:
        AddressValidationIssue<"${path}.postal_address">,
}