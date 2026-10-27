import "../../../common/error/validation/WorkerValidationIssue.tsp";
import "../../../common/error/validation/OrganizationValidationIssue.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Workers.Errors;

@doc("Все нарушения полей создания работника. organization и organization_id могут отсутствовать, но не могут передаваться одновременно.")
union CreateWorkerValidationIssue {
    worker: WorkerValidationIssue,

    organization_id: {
        field: "organization_id";
        code: "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE" | "MUTUALLY_EXCLUSIVE";
        message: string;
    },

    organization: {
        field: "organization";
        code: "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "MUTUALLY_EXCLUSIVE";
        message: string;
    },

    organization_fields: OrganizationValidationIssue<"organization">,

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
