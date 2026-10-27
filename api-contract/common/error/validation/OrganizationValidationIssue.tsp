import "./AddressValidationIssue.tsp";

namespace WorkersApi.Errors;

@doc("Нарушения полей организации и вложенных адресов.")
union OrganizationValidationIssue<path extends string, separator extends string = "."> {
    official_address: {
        field: "${path}${separator}official_address";
        code: "INVALID_COMBINATION";
        message: string;
    },

    official_address_id: {
        field: "${path}${separator}official_address_id";
        code: "MUST_BE_POSITIVE";
        message: string;
    },

    annual_turnover: {
        field: "${path}${separator}annual_turnover";
        code: "MUST_NOT_BE_NULL" | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        message: string;
    },

    employees_count: {
        field: "${path}${separator}employees_count";
        code: "MUST_NOT_BE_NULL" | "MUST_BE_POSITIVE";
        message: string;
    },

    full_name: {
        field: "${path}${separator}full_name";
        code: "MUST_NOT_BE_EMPTY";
        message: string;
    },

    rating: {
        field: "${path}${separator}rating";
        code: "MUST_NOT_BE_NULL" | "MUST_BE_POSITIVE";
        message: string;
    },

    postal_address: {
        field: "${path}${separator}postal_address";
        code: "INVALID_COMBINATION";
        message: string;
    },

    postal_address_id: {
        field: "${path}${separator}postal_address_id";
        code: "MUST_BE_POSITIVE";
        message: string;
    },

    official_address_fields:
        AddressValidationIssue<"${path}${separator}official_address">,

    postal_address_fields:
        AddressValidationIssue<"${path}${separator}postal_address">,
}
