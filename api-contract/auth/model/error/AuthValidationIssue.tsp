import "../../../common/error/BadRequestError.tsp";
import "../../../common/error/ValidationError.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Auth.Errors;

const authValidationIssueExample = #{
    field: "password",
    title: "MUST_NOT_BE_EMPTY",
    description: "Password must not be empty."
};

const authBadRequestExample: BadRequestError = #{
    statusCode: 400,
    contentType: "application/json",
    body: #{
        type: "/problems/malformed-json",
        title: "Malformed JSON",
        status: 400,
        detail: "The request body contains invalid JSON."
    }
};

const credentialsValidationErrorExample:
    ValidationError<422, AuthValidationIssue> = #{
        statusCode: 422,
        contentType: "application/json",
        body: #{
            type: "/problems/validation-failed",
            title: "Validation failed",
            status: 422,
            errors: #[authValidationIssueExample]
        }
    };

const refreshTokenValidationErrorExample:
    ValidationError<422, AuthValidationIssue> = #{
        statusCode: 422,
        contentType: "application/json",
        body: #{
            type: "/problems/validation-failed",
            title: "Validation failed",
            status: 422,
            errors: #[
                #{
                    field: "refresh_token",
                    title: "MUST_NOT_BE_EMPTY",
                    description: "Refresh cookie must not be empty."
                }
            ]
        }
    };

const authBadRequestOperationExample = #{
    returnType: authBadRequestExample
};

const credentialsValidationOperationExample = #{
    returnType: credentialsValidationErrorExample
};

const refreshTokenValidationOperationExample = #{
    returnType: refreshTokenValidationErrorExample
};

@doc("Ошибки полей аутентификации.")
@example(authValidationIssueExample)
union AuthValidationIssue {
    username: {
        field: "username";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_NOT_BE_EMPTY";
        description: string;
    },

    password: {
        field: "password";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_NOT_BE_EMPTY";
        description: string;
    },

    refresh_token: {
        field: "refresh_token";
        title: "REQUIRED" | "MUST_NOT_BE_EMPTY";
        description: string;
    },
}