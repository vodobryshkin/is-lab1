namespace WorkersApi.Errors;

@doc("Нарушения полей локации.")
union LocationValidationIssue<path extends string, separator extends string = "."> {
    id: {
        field: "${path}${separator}id";
        title: "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "MUST_BE_POSITIVE"
            | "OUT_OF_RANGE" | "MUTUALLY_EXCLUSIVE";
        description: string;
    },

    x: {
        field: "${path}${separator}x";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "OUT_OF_RANGE";
        description: string;
    },

    y: {
        field: "${path}${separator}y";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "OUT_OF_RANGE";
        description: string;
    },

    z: {
        field: "${path}${separator}z";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "OUT_OF_RANGE";
        description: string;
    },
}
