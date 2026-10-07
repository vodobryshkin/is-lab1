import "./AddressValidationIssue.tsp";

namespace WorkersApi.Errors;

@doc("Нарушения полей организации и вложенных адресов.")
union OrganizationValidationIssue<path extends string, separator extends string = "."> {
    official_address: {
        field: "${path}${separator}official_address";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "MUTUALLY_EXCLUSIVE";
        description: string;
    },

    official_address_id: {
        field: "${path}${separator}official_address_id";
        title: "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE" | "MUTUALLY_EXCLUSIVE";
        description: string;
    },

    annual_turnover: {
        field: "${path}${separator}annual_turnover";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        description: string;
    },

    employees_count: {
        field: "${path}${separator}employees_count";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        description: string;
    },

    full_name: {
        field: "${path}${separator}full_name";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_NOT_BE_EMPTY" | "MUST_MATCH_PATTERN";
        description: string;
    },

    rating: {
        field: "${path}${separator}rating";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        description: string;
    },

    type: {
        field: "${path}${separator}type";
        title: "REQUIRED" | "INVALID_TYPE" | "INVALID_ENUM_VALUE";
        description: string;
    },

    postal_address: {
        field: "${path}${separator}postal_address";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "MUTUALLY_EXCLUSIVE";
        description: string;
    },

    postal_address_id: {
        field: "${path}${separator}postal_address_id";
        title: "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE" | "MUTUALLY_EXCLUSIVE";
        description: string;
    },

    official_address_fields:
        AddressValidationIssue<"${path}${separator}official_address">,

    postal_address_fields:
        AddressValidationIssue<"${path}${separator}postal_address">,

    unknown_field: {
        field: string;
        title: "UNKNOWN_FIELD";
        description: string;
    },
}