import "../../../common/error/ProblemResponse.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Workers.Errors;

const createWorkerOrganizationNotFoundErrorExample:
    CreateWorkerOrganizationNotFoundError = #{
        statusCode: 404,
        contentType: "application/json",
        body: #{
            type: "/problems/organization-not-found",
            title: "Organization not found",
            status: 404,
            detail: "Organization with id=999 does not exist.",
            organization_id: 999
        }
    };

@error
@doc("Организация не найдена.")
@example(createWorkerOrganizationNotFoundErrorExample)
model CreateWorkerOrganizationNotFoundError is ProblemResponse<
    404,
    {
        ...OmitProperties<ProblemDetails<404>, "type">;

        type: "/problems/organization-not-found";

        @minValueExclusive(0)
        organization_id: int32;
    }
>;