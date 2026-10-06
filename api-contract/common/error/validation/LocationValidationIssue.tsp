namespace WorkersApi.Errors;

@doc("Нарушения полей локации.")
union LocationValidationIssue<path extends string> {
    x: {
        field: "${path}.x";
        title: "required" | "must_not_be_null"
            | "invalid_type" | "out_of_range";
        description: string;
    },

    y: {
        field: "${path}.y";
        title: "required" | "must_not_be_null"
            | "invalid_type" | "out_of_range";
        description: string;
    },

    z: {
        field: "${path}.z";
        title: "required" | "must_not_be_null"
            | "invalid_type" | "out_of_range";
        description: string;
    },
}