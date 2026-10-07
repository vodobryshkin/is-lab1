import "../../../common/error/BadRequestError.tsp";
import "../../../common/error/AddressNotFoundError.tsp";
import "../../../common/error/UnauthorizedError.tsp";
import "../../../common/error/ForbiddenError.tsp";
import "../../../common/error/ValidationError.tsp";
import "../../../common/error/validation/AddressValidationIssue.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Addresses.Errors;

const addressBadRequestOperationExample = #{
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

const addressInvalidIdOperationExample = #{
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
                    description: "Address ID must be greater than zero."
                }
            ]
        }
    }
};

const addressMissingTokenOperationExample = #{
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

const addressInvalidTokenOperationExample = #{
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

const addressForbiddenOperationExample = #{
    returnType: #{
        statusCode: 403,
        contentType: "application/json",
        body: #{
            type: "/problems/insufficient-permissions",
            title: "Insufficient permissions",
            status: 403,
            detail: "Modifying addresses is not allowed."
        }
    }
};

const addressNotFoundOperationExample = #{
    returnType: addressNotFoundErrorExample
};

const deleteAddressConflictErrorExample: DeleteAddressConflictError = #{
    statusCode: 409,
    contentType: "application/json",
    body: #{
        type: "/problems/address-is-in-use",
        title: "Address is in use",
        status: 409,
        detail: "The address cannot be deleted while organizations reference it.",
        address_id: 42
    }
};

const addressValidationErrorExample: AddressValidationError = #{
    statusCode: 422,
    contentType: "application/json",
    body: #{
        type: "/problems/validation-failed",
        title: "Validation failed",
        status: 422,
        errors: #[
            #{
                field: "street",
                title: "MUST_NOT_BE_EMPTY",
                description: "Street must not be empty."
            },
            #{
                field: "town.x",
                title: "OUT_OF_RANGE",
                description: "Town x must be within the int32 range."
            }
        ]
    }
};

const deleteAddressConflictOperationExample = #{
    returnType: deleteAddressConflictErrorExample
};

const addressValidationOperationExample = #{
    returnType: addressValidationErrorExample
};

@doc("Ошибки данных адреса.")
union AddressRequestValidationIssue {
    address: AddressValidationIssue<"", "">,

    body: {
        field: "$";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE";
        description: string;
    },
}

@error
@doc("Адрес используется организациями.")
@example(deleteAddressConflictErrorExample)
model DeleteAddressConflictError is ProblemResponse<
    409,
    {
        ...OmitProperties<ProblemDetails<409>, "type">;

        type: "/problems/address-is-in-use";

        @doc("ID адреса.")
        @minValue(1)
        address_id: int32;
    }
>;

@error
@doc("Ошибка валидации адреса.")
@example(addressValidationErrorExample)
model AddressValidationError is
    ValidationError<422, AddressRequestValidationIssue>;