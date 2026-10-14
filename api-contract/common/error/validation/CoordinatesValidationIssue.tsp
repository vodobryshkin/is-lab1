namespace WorkersApi.Errors;

@doc("Нарушения полей координат работника.")
union CoordinatesValidationIssue<path extends string, separator extends string = "."> {
    id: {
        field: "${path}${separator}id";
        title: "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "MUST_BE_POSITIVE"
            | "OUT_OF_RANGE" | "MUTUALLY_EXCLUSIVE";
        description: string;
    },

    x: {
        field: "${path}${separator}x";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "OUT_OF_RANGE" | "MUST_BE_GREATER_THAN_MINUS_954";
        description: string;
    },

    y: {
        field: "${path}${separator}y";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "OUT_OF_RANGE" | "MUST_BE_GREATER_THAN_MINUS_392";
        description: string;
    },
}
