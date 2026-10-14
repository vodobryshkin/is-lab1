import "./LocationValidationIssue.tsp";

namespace WorkersApi.Errors;

@doc("Нарушения полей персоны и вложенной локации.")
union PersonValidationIssue<path extends string, separator extends string = "."> {
    id: {
        field: "${path}${separator}id";
        title: "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "MUST_BE_POSITIVE"
            | "OUT_OF_RANGE" | "MUTUALLY_EXCLUSIVE";
        description: string;
    },

    eye_color: {
        field: "${path}${separator}eye_color";
        title: "REQUIRED" | "INVALID_TYPE" | "INVALID_ENUM_VALUE";
        description: string;
    },

    hair_color: {
        field: "${path}${separator}hair_color";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "INVALID_ENUM_VALUE";
        description: string;
    },

    location: {
        field: "${path}${separator}location";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE";
        description: string;
    },

    birthday: {
        field: "${path}${separator}birthday";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "INVALID_DATE_TIME";
        description: string;
    },

    weight: {
        field: "${path}${separator}weight";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        description: string;
    },

    nationality: {
        field: "${path}${separator}nationality";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "INVALID_ENUM_VALUE";
        description: string;
    },

    location_fields: LocationValidationIssue<"${path}${separator}location">,
}
