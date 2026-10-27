namespace WorkersApi.Errors;

@doc("Нарушения полей локации.")
union LocationValidationIssue<path extends string, separator extends string = "."> {
    id: {
        field: "${path}${separator}id";
        code: "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "MUST_BE_POSITIVE"
            | "OUT_OF_RANGE" | "MUTUALLY_EXCLUSIVE";
        message: string;
    },

    x: {
        field: "${path}${separator}x";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "OUT_OF_RANGE";
        message: string;
    },

    y: {
        field: "${path}${separator}y";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "OUT_OF_RANGE";
        message: string;
    },

    z: {
        field: "${path}${separator}z";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "OUT_OF_RANGE";
        message: string;
    },
}
