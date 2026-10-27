import "../../../common/error/validation/WorkerValidationIssue.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Workers.Errors;

@doc("Нарушения полей обновления работника.")
union UpdateWorkerValidationIssue {
    worker: WorkerValidationIssue,

    organization_id: {
        field: "organization_id";
        code: "MUST_BE_POSITIVE";
        message: string;
    },
}