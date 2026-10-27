namespace WorkersApi.Errors;

@doc("Нарушения полей координат работника.")
union CoordinatesValidationIssue<path extends string, separator extends string = "."> {
    id: {
        field: "${path}${separator}id";
        code: "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "MUST_BE_POSITIVE"
            | "OUT_OF_RANGE" | "MUTUALLY_EXCLUSIVE";
        message: string;
    },

    x: {
        field: "${path}${separator}x";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "OUT_OF_RANGE" | "MUST_BE_GREATER_THAN_MINUS_954";
        message: string;
    },

    y: {
        field: "${path}${separator}y";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "OUT_OF_RANGE" | "MUST_BE_GREATER_THAN_MINUS_392";
        message: string;
    },
}
