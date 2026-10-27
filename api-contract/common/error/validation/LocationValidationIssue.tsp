namespace WorkersApi.Errors;

@doc("Нарушения полей локации. У ссылки по ID проверяется только id.")
union LocationValidationIssue<path extends string, separator extends string = "."> {
    id: {
        field: "${path}${separator}id";
        code: "MUST_BE_POSITIVE";
        message: string;
    },

    x: {
        field: "${path}${separator}x";
        code: "MUST_NOT_BE_NULL";
        message: string;
    },

    y: {
        field: "${path}${separator}y";
        code: "MUST_NOT_BE_NULL";
        message: string;
    },

    z: {
        field: "${path}${separator}z";
        code: "MUST_NOT_BE_NULL";
        message: string;
    },
}
