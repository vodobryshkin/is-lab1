import "./ValidationError.tsp";

namespace WorkersApi.Errors;

@doc("Нарушения тела запроса специальной операции.")
union SpecialRequestValidationIssue {
    organization_id: {
        field: "organization_id";
        code: "MUST_NOT_BE_NULL" | "MUST_BE_POSITIVE";
        message: string;
    },

    coefficient: {
        field: "coefficient";
        code: "MUST_NOT_BE_NULL" | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        message: string;
    },
}
