import "../../../common/error/BadRequestError.tsp";
import "../../../common/error/UnauthorizedError.tsp";
import "../../../common/error/ForbiddenError.tsp";
import "../../../common/error/ValidationError.tsp";
import "../../../common/error/validation/OrganizationValidationIssue.tsp";
import "../../../common/error/AddressNotFoundError.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Organizations.Errors;

const organizationAddressNotFoundOperationExample = #{
    returnType: addressNotFoundErrorExample
};

const organizationAddressOptionsValidationOperationExample = #{
    returnType: #{
        ...organizationValidationErrorExample,
        body: #{
            type: "/problems/validation-failed",
            title: "Validation failed",
            status: 422,
            errors: #[
                #{
                    field: "official_address_id",
                    title: "MUTUALLY_EXCLUSIVE",
                    description: "Provide official_address_id or official_address, not both."
                }
            ]
        }
    }
};

const organizationBadRequestOperationExample = #{
    returnType: #{
        statusCode: 400,
        contentType: "application/json",
        body: #{
            type: "/problems/malformed-json",
            title: "Malformed JSON",
            status: 400,
            detail: "The request body contains invalid JSON."
        }
    }
};

const organizationInvalidIdOperationExample = #{
    returnType: #{
        statusCode: 400,
        contentType: "application/json",
        body: #{
            type: "/problems/validation-failed",
            title: "Invalid request parameters",
            status: 400,
            errors: #[
                #{
                    field: "id",
                    title: "MUST_BE_POSITIVE",
                    description: "Organization ID must be greater than zero."
                }
            ]
        }
    }
};

const organizationMissingTokenOperationExample = #{
    returnType: #{
        statusCode: 401,
        wwwAuthenticate: "Bearer realm=\"Workers API\"",
        contentType: "application/problem+json",
        body: #{
            type: "/problems/missing-token",
            title: "Authentication required",
            status: 401,
            detail: "A bearer token is required."
        }
    }
};

const organizationInvalidTokenOperationExample = #{
    returnType: #{
        statusCode: 401,
        wwwAuthenticate: "Bearer realm=\"Workers API\", error=\"invalid_token\"",
        contentType: "application/problem+json",
        body: #{
            type: "/problems/invalid-token",
            title: "Invalid token",
            status: 401,
            detail: "The bearer token is invalid or expired."
        }
    }
};

const organizationForbiddenOperationExample = #{
    returnType: #{
        statusCode: 403,
        contentType: "application/json",
        body: #{
            type: "/problems/insufficient-permissions",
            title: "Insufficient permissions",
            status: 403,
            detail: "Modifying organizations is not allowed."
        }
    }
};

const organizationNotFoundErrorExample: OrganizationNotFoundError = #{
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

const organizationNameConflictErrorExample: OrganizationNameConflictError = #{
    statusCode: 409,
    contentType: "application/json",
    body: #{
        type: "/problems/organization-name-already-exists",
        title: "Organization name already exists",
        status: 409,
        detail: "Another organization with this full_name already exists.",
        full_name: "Example Corporation"
    }
};

const deleteOrganizationConflictErrorExample: DeleteOrganizationConflictError = #{
    statusCode: 409,
    contentType: "application/json",
    body: #{
        type: "/problems/organization-has-workers",
        title: "Organization has workers",
        status: 409,
        detail: "The organization cannot be deleted while workers reference it.",
        organization_id: 42
    }
};

const organizationValidationErrorExample: OrganizationValidationError = #{
    statusCode: 422,
    contentType: "application/json",
    body: #{
        type: "/problems/validation-failed",
        title: "Validation failed",
        status: 422,
        errors: #[
            #{
                field: "annual_turnover",
                title: "MUST_BE_POSITIVE",
                description: "Annual turnover must be greater than zero."
            },
            #{
                field: "official_address.street",
                title: "MUST_NOT_BE_EMPTY",
                description: "Street must not be empty."
            }
        ]
    }
};

const organizationNotFoundOperationExample = #{
    returnType: organizationNotFoundErrorExample
};

const organizationNameConflictOperationExample = #{
    returnType: organizationNameConflictErrorExample
};

const deleteOrganizationConflictOperationExample = #{
    returnType: deleteOrganizationConflictErrorExample
};

const organizationValidationOperationExample = #{
    returnType: organizationValidationErrorExample
};

@doc("Ошибки данных организации.")
union OrganizationRequestValidationIssue {
    organization: OrganizationValidationIssue<"", "">,

    body: {
        field: "$";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE";
        description: string;
    },
}

@error
@doc("Организация не найдена.")
@example(organizationNotFoundErrorExample)
model OrganizationNotFoundError is ProblemResponse<
    404,
    {
        ...OmitProperties<ProblemDetails<404>, "type">;

        type: "/problems/organization-not-found";

        @doc("ID организации.")
        @minValue(1)
        organization_id: int32;
    }
>;

@error
@doc("Название организации занято.")
@example(organizationNameConflictErrorExample)
model OrganizationNameConflictError is ProblemResponse<
    409,
    {
        ...OmitProperties<ProblemDetails<409>, "type">;

        type: "/problems/organization-name-already-exists";

        @doc("Занятое название организации.")
        full_name: string;
    }
>;

@error
@doc("На организацию ссылаются работники.")
@example(deleteOrganizationConflictErrorExample)
model DeleteOrganizationConflictError is ProblemResponse<
    409,
    {
        ...OmitProperties<ProblemDetails<409>, "type">;

        type: "/problems/organization-has-workers";

        @doc("ID организации.")
        @minValue(1)
        organization_id: int32;
    }
>;

@error
@doc("Ошибка валидации организации.")
@example(organizationValidationErrorExample)
model OrganizationValidationError is
    ValidationError<422, OrganizationRequestValidationIssue>;