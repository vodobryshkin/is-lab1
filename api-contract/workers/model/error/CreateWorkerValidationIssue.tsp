import "../../../common/error/validation/WorkerValidationIssue.tsp";
import "../../../common/error/validation/OrganizationValidationIssue.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Workers.Errors;

@doc("Все нарушения полей создания работника. organization и organization_id могут отсутствовать, но не могут передаваться одновременно.")
union CreateWorkerValidationIssue {
    worker: WorkerValidationIssue,

    organization_id: {
        field: "organization_id";
        title: "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE" | "MUTUALLY_EXCLUSIVE";
        description: string;
    },

    organization: {
        field: "organization";
        title: "MUST_NOT_BE_NULL" | "INVALID_TYPE" | "MUTUALLY_EXCLUSIVE";
        description: string;
    },

    organization_fields: OrganizationValidationIssue<"organization">,
}