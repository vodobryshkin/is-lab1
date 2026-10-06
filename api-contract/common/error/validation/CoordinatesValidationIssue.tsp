namespace WorkersApi.Errors;

@doc("Нарушения полей координат работника.")
union CoordinatesValidationIssue<path extends string> {
    x: {
        field: "${path}.x";
        title: "required" | "must_not_be_null" | "invalid_type"
            | "out_of_range" | "must_be_greater_than_minus_954";
        description: string;
    },

    y: {
        field: "${path}.y";
        title: "required" | "must_not_be_null" | "invalid_type"
            | "out_of_range" | "must_be_greater_than_minus_392";
        description: string;
    },
}