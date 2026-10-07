import "./LocationValidationIssue.tsp";

namespace WorkersApi.Errors;

@doc("Нарушения полей адреса и вложенной локации.")
union AddressValidationIssue<path extends string, separator extends string = "."> {
    street: {
        field: "${path}${separator}street";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_NOT_BE_EMPTY" | "MUST_MATCH_PATTERN";
        description: string;
    },

    town: {
        field: "${path}${separator}town";
        title: "REQUIRED" | "INVALID_TYPE";
        description: string;
    },

    town_fields: LocationValidationIssue<"${path}${separator}town">,
}