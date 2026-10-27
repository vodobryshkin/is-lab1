import "./AddressValidationIssue.tsp";

namespace WorkersApi.Errors;

@doc("Нарушения полей организации и вложенных адресов.")
union OrganizationValidationIssue<path extends string, separator extends string = "."> {
    official_address: {
        field: "${path}${separator}official_address";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "MUTUALLY_EXCLUSIVE";
        message: string;
    },

    official_address_id: {
        field: "${path}${separator}official_address_id";
        code: "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE" | "MUTUALLY_EXCLUSIVE";
        message: string;
    },

    annual_turnover: {
        field: "${path}${separator}annual_turnover";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        message: string;
    },

    employees_count: {
        field: "${path}${separator}employees_count";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        message: string;
    },

    full_name: {
        field: "${path}${separator}full_name";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_NOT_BE_EMPTY" | "MUST_MATCH_PATTERN";
        message: string;
    },

    rating: {
        field: "${path}${separator}rating";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        message: string;
    },

    type: {
        field: "${path}${separator}type";
        code: "REQUIRED" | "INVALID_TYPE" | "INVALID_ENUM_VALUE";
        message: string;
    },

    postal_address: {
        field: "${path}${separator}postal_address";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "MUTUALLY_EXCLUSIVE";
        message: string;
    },

    postal_address_id: {
        field: "${path}${separator}postal_address_id";
        code: "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE" | "MUTUALLY_EXCLUSIVE";
        message: string;
    },

    official_address_fields:
        AddressValidationIssue<"${path}${separator}official_address">,

    postal_address_fields:
        AddressValidationIssue<"${path}${separator}postal_address">,

    unknown_field: {
        field: string;
        code: "UNKNOWN_FIELD";
        message: string;
    },
}