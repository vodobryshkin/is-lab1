import "./LocationValidationIssue.tsp";

namespace WorkersApi.Errors;

@doc("Нарушения полей персоны и вложенной локации. У ссылки по ID проверяется только id.")
union PersonValidationIssue<path extends string, separator extends string = "."> {
    id: {
        field: "${path}${separator}id";
        code: "MUST_BE_POSITIVE";
        message: string;
    },

    hair_color: {
        field: "${path}${separator}hair_color";
        code: "MUST_NOT_BE_NULL";
        message: string;
    },

    location: {
        field: "${path}${separator}location";
        code: "MUST_NOT_BE_NULL";
        message: string;
    },

    birthday: {
        field: "${path}${separator}birthday";
        code: "MUST_NOT_BE_NULL";
        message: string;
    },

    weight: {
        field: "${path}${separator}weight";
        code: "MUST_NOT_BE_NULL" | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        message: string;
    },

    nationality: {
        field: "${path}${separator}nationality";
        code: "MUST_NOT_BE_NULL";
        message: string;
    },

    location_fields: LocationValidationIssue<"${path}${separator}location">,
}
