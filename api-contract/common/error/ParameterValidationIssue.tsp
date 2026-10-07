namespace WorkersApi.Errors;

const parameterValidationIssueExample = #{
    field: "page",
    title: "MUST_BE_POSITIVE",
    description: "Page must be greater than zero."
};

@doc("Ошибки параметров запроса.")
@example(parameterValidationIssueExample)
union ParameterValidationIssue {
    id: {
        field: "id";
        title: "INVALID_TYPE" | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        description: string;
    },

    worker_id: {
        field: "worker_id";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        description: string;
    },

    organization_id: {
        field: "organization_id";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        description: string;
    },

    page: {
        field: "page";
        title: "INVALID_TYPE" | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        description: string;
    },

    page_size: {
        field: "page_size";
        title: "INVALID_TYPE" | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        description: string;
    },

    sort_by: {
        field: "sort_by";
        title: "INVALID_TYPE" | "INVALID_ENUM_VALUE";
        description: string;
    },

    sort_order: {
        field: "sort_order";
        title: "INVALID_TYPE" | "INVALID_ENUM_VALUE";
        description: string;
    },

    status: {
        field: "status";
        title: "REQUIRED" | "INVALID_TYPE" | "INVALID_ENUM_VALUE";
        description: string;
    },

    rating: {
        field: "rating";
        title: "REQUIRED" | "INVALID_TYPE" | "OUT_OF_RANGE";
        description: string;
    },

    coefficient: {
        field: "coefficient";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        description: string;
    },
}