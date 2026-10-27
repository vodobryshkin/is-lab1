import "../../../common/error/BadRequestError.tsp";
import "../../../common/error/UnauthorizedError.tsp";
import "../../../common/error/ForbiddenError.tsp";
import "../../../common/error/NotFoundError.tsp";
import "../../../common/error/ConflictError.tsp";
import "../../../common/error/ValidationError.tsp";
import "../../../common/error/validation/OrganizationValidationIssue.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Organizations.Errors;

const organizationAddressNotFoundOperationExample = #{
    returnType: addressNotFoundExample
};

const organizationAddressOptionsValidationOperationExample = #{
    returnType: #{
        ...organizationValidationErrorExample,
        body: #{
            errors: #[
                #{
                    field: "official_address",
                    code: "INVALID_COMBINATION",
                    message: "Use either official_address or official_address_id."
                }
            ]
        }
    }
};

const organizationBadRequestOperationExample = #{
    returnType: badRequestExample
};

const organizationInvalidIdOperationExample = #{
    returnType: invalidIdExample
};

const deleteOrganizationConflictOperationExample = #{
    returnType: #{
        statusCode: 409,
        contentType: "application/json",
        body: #{
            message: "The organization cannot be deleted while workers reference it."
        }
    }
};

const salaryIndexationConflictOperationExample = #{
    returnType: #{
        statusCode: 409,
        contentType: "application/json",
        body: #{
            message: "Indexation would produce a salary outside the allowed range."
        }
    }
};

const organizationValidationErrorExample: OrganizationValidationError = #{
    statusCode: 422,
    contentType: "application/json",
    body: #{
        errors: #[
            #{
                field: "annual_turnover",
                code: "MUST_BE_POSITIVE",
                message: "annual_turnover must be greater than zero."
            },
            #{
                field: "official_address.street",
                code: "MUST_NOT_BE_EMPTY",
                message: "official_address.street must not be empty."
            }
        ]
    }
};

const organizationValidationOperationExample = #{
    returnType: organizationValidationErrorExample
};

@doc("Ошибки данных организации.")
union OrganizationRequestValidationIssue {
    organization: OrganizationValidationIssue<"", "">,
}

@error
@doc("Ошибка валидации организации.")
@example(organizationValidationErrorExample)
model OrganizationValidationError is
    ValidationError<OrganizationRequestValidationIssue>;
