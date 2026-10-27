import "../../../common/error/validation/WorkerValidationIssue.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Workers.Errors;

@doc("Нарушения полей обновления работника.")
union UpdateWorkerValidationIssue {
    worker: WorkerValidationIssue,

    organization_id: {
        field: "organization_id";
        code: "REQUIRED" | "INVALID_TYPE" | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
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