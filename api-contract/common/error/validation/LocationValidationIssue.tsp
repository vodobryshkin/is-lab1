namespace WorkersApi.Errors;

@doc("Нарушения полей локации.")
union LocationValidationIssue<path extends string> {
    x: {
        field: "${path}.x";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "OUT_OF_RANGE";
        description: string;
    },

    y: {
        field: "${path}.y";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "OUT_OF_RANGE";
        description: string;
    },

    z: {
        field: "${path}.z";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "OUT_OF_RANGE";
        description: string;
    },
}