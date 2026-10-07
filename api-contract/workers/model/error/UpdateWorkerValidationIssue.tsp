import "../../../common/error/validation/WorkerValidationIssue.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Workers.Errors;

@doc("Нарушения полей обновления работника.")
union UpdateWorkerValidationIssue {
    worker: WorkerValidationIssue,

    organization_id: {
        field: "organization_id";
        title: "REQUIRED" | "INVALID_TYPE" | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        description: string;
    },

    body: {
        field: "$";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE";
        description: string;
    },

    unknown_field: {
        field: string;
        title: "UNKNOWN_FIELD";
        description: string;
    },
}