import "./LocationValidationIssue.tsp";

namespace WorkersApi.Errors;

@doc("Нарушения полей персоны и вложенной локации.")
union PersonValidationIssue<path extends string> {
    eye_color: {
        field: "${path}.eye_color";
        title: "required" | "invalid_type" | "invalid_enum_value";
        description: string;
    },

    hair_color: {
        field: "${path}.hair_color";
        title: "required" | "must_not_be_null"
            | "invalid_type" | "invalid_enum_value";
        description: string;
    },

    location: {
        field: "${path}.location";
        title: "required" | "must_not_be_null" | "invalid_type";
        description: string;
    },

    birthday: {
        field: "${path}.birthday";
        title: "required" | "must_not_be_null"
            | "invalid_type" | "invalid_date_time";
        description: string;
    },

    weight: {
        field: "${path}.weight";
        title: "required" | "must_not_be_null" | "invalid_type"
            | "must_be_positive" | "out_of_range";
        description: string;
    },

    nationality: {
        field: "${path}.nationality";
        title: "required" | "must_not_be_null"
            | "invalid_type" | "invalid_enum_value";
        description: string;
    },

    location_fields: LocationValidationIssue<"${path}.location">,
}