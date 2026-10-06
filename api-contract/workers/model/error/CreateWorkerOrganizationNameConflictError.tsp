import "../../../common/error/ProblemResponse.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Workers.Errors;

const createWorkerOrganizationNameConflictErrorExample:
    CreateWorkerOrganizationNameConflictError = #{
        statusCode: 409,
        contentType: "application/json",
        body: #{
            type: "/problems/organization-name-already-exists",
            title: "Organization name already exists",
            status: 409,
            detail: "An organization with this full_name already exists.",
            full_name: "Example Corporation"
        }
    };

@error
@doc("Название организации занято.")
@example(createWorkerOrganizationNameConflictErrorExample)
model CreateWorkerOrganizationNameConflictError is ProblemResponse<
    409,
    {
        ...OmitProperties<ProblemDetails<409>, "type">;

        type: "/problems/organization-name-already-exists";

        full_name: string;
    }
>;