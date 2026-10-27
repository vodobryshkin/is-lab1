import "./ValidationError.tsp";

namespace WorkersApi.Errors;

@doc("Нарушения тела запроса специальной операции.")
union SpecialRequestValidationIssue {
    organization_id: {
        field: "organization_id";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        message: string;
    },

    coefficient: {
        field: "coefficient";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        message: string;
    },

    body: {
        field: "$";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE";
        message: string;
    },

    unknown_field: {
        field: string;
        code: "UNKNOWN_FIELD";
        message: string;
    },
}
