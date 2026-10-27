import "../../../common/error/BadRequestError.tsp";
import "../../../common/error/UnauthorizedError.tsp";
import "../../../common/error/ForbiddenError.tsp";
import "../../../common/error/NotFoundError.tsp";
import "../../../common/error/ConflictError.tsp";
import "../../../common/error/ValidationError.tsp";
import "../../../common/error/validation/AddressValidationIssue.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Addresses.Errors;

const addressBadRequestOperationExample = #{
    returnType: #{
        statusCode: 400,
        contentType: "application/json",
        body: #{
            message: "The request body contains invalid JSON or a value of the wrong type."
        }
    }
};

const addressInvalidIdOperationExample = #{
    returnType: #{
        statusCode: 400,
        contentType: "application/json",
        body: #{
            message: "Parameter id must be greater than zero."
        }
    }
};

const deleteAddressConflictErrorExample: ConflictError = #{
    statusCode: 409,
    contentType: "application/json",
    body: #{
        message: "The address cannot be deleted while organizations reference it."
    }
};

const addressValidationErrorExample: AddressValidationError = #{
    statusCode: 422,
    contentType: "application/json",
    body: #{
        errors: #[
            #{
                field: "street",
                code: "MUST_NOT_BE_EMPTY",
                message: "street must not be empty."
            },
            #{
                field: "town.x",
                code: "MUST_NOT_BE_NULL",
                message: "town.x must not be null."
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
}

@error
@doc("Ошибка валидации адреса.")
@example(addressValidationErrorExample)
model AddressValidationError is
    ValidationError<AddressRequestValidationIssue>;
