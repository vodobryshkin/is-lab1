import "./LocationValidationIssue.tsp";

namespace WorkersApi.Errors;

@doc("Нарушения полей адреса и вложенной локации.")
union AddressValidationIssue<path extends string> {
    street: {
        field: "${path}.street";
        title: "required" | "must_not_be_null" | "invalid_type"
            | "must_not_be_empty" | "must_match_pattern";
        description: string;
    },

    town: {
        field: "${path}.town";
        title: "required" | "invalid_type";
        description: string;
    },

    town_fields: LocationValidationIssue<"${path}.town">,
}