import "./LocationValidationIssue.tsp";

namespace WorkersApi.Errors;

@doc("Нарушения полей персоны и вложенной локации.")
union PersonValidationIssue<path extends string, separator extends string = "."> {
    id: {
        field: "${path}${separator}id";
        code: "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "MUST_BE_POSITIVE"
            | "OUT_OF_RANGE" | "MUTUALLY_EXCLUSIVE";
        message: string;
    },

    eye_color: {
        field: "${path}${separator}eye_color";
        code: "REQUIRED" | "INVALID_TYPE" | "INVALID_ENUM_VALUE";
        message: string;
    },

    hair_color: {
        field: "${path}${separator}hair_color";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "INVALID_ENUM_VALUE";
        message: string;
    },

    location: {
        field: "${path}${separator}location";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE";
        message: string;
    },

    birthday: {
        field: "${path}${separator}birthday";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "INVALID_DATE_TIME";
        message: string;
    },

    weight: {
        field: "${path}${separator}weight";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        message: string;
    },

    nationality: {
        field: "${path}${separator}nationality";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "INVALID_ENUM_VALUE";
        message: string;
    },

    location_fields: LocationValidationIssue<"${path}${separator}location">,
}
