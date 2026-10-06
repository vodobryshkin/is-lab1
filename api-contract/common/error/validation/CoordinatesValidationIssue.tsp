namespace WorkersApi.Errors;

@doc("Нарушения полей координат работника.")
union CoordinatesValidationIssue<path extends string> {
    x: {
        field: "${path}.x";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "OUT_OF_RANGE" | "MUST_BE_GREATER_THAN_MINUS_954";
        description: string;
    },

    y: {
        field: "${path}.y";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "OUT_OF_RANGE" | "MUST_BE_GREATER_THAN_MINUS_392";
        description: string;
    },
}