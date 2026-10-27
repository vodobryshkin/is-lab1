import "../../../common/error/validation/WorkerValidationIssue.tsp";
import "../../../common/error/validation/OrganizationValidationIssue.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Workers.Errors;

@doc("Нарушения полей создания работника. organization и organization_id нельзя передавать одновременно.")
union CreateWorkerValidationIssue {
    worker: WorkerValidationIssue,

    organization_id: {
        field: "organization_id";
        code: "MUST_BE_POSITIVE";
        message: string;
    },

    organization: {
        field: "organization";
        code: "INVALID_COMBINATION";
        message: string;
    },

    organization_fields: OrganizationValidationIssue<"organization">,
}
