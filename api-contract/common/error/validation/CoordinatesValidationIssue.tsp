namespace WorkersApi.Errors;

@doc("Нарушения полей координат. У ссылки по ID проверяется только id.")
union CoordinatesValidationIssue<path extends string, separator extends string = "."> {
    id: {
        field: "${path}${separator}id";
        code: "MUST_BE_POSITIVE";
        message: string;
    },

    x: {
        field: "${path}${separator}x";
        code: "MUST_NOT_BE_NULL" | "MUST_BE_GREATER_THAN_MINUS_954";
        message: string;
    },

    y: {
        field: "${path}${separator}y";
        code: "MUST_NOT_BE_NULL" | "MUST_BE_GREATER_THAN_MINUS_392";
        message: string;
    },
}
