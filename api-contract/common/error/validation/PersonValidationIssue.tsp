import "./LocationValidationIssue.tsp";

namespace WorkersApi.Errors;

@doc("Нарушения полей персоны и вложенной локации.")
union PersonValidationIssue<path extends string> {
    eye_color: {
        field: "${path}.eye_color";
        title: "REQUIRED" | "INVALID_TYPE" | "INVALID_ENUM_VALUE";
        description: string;
    },

    hair_color: {
        field: "${path}.hair_color";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "INVALID_ENUM_VALUE";
        description: string;
    },

    location: {
        field: "${path}.location";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE";
        description: string;
    },

    birthday: {
        field: "${path}.birthday";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "INVALID_DATE_TIME";
        description: string;
    },

    weight: {
        field: "${path}.weight";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        description: string;
    },

    nationality: {
        field: "${path}.nationality";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "INVALID_ENUM_VALUE";
        description: string;
    },

    location_fields: LocationValidationIssue<"${path}.location">,
}