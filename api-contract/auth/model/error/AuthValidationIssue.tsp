import "../../../common/error/BadRequestError.tsp";
import "../../../common/error/ValidationError.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Auth.Errors;

const authValidationIssueExample = #{
    field: "password",
    title: "OUT_OF_RANGE",
    description: "Password must contain between 6 and 32 characters."
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

const authBadRequestOperationExample = #{
    returnType: authBadRequestExample
};

const credentialsValidationOperationExample = #{
    returnType: credentialsValidationErrorExample
};

@doc("Ошибки полей аутентификации.")
@example(authValidationIssueExample)
union AuthValidationIssue {
    username: {
        field: "username";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_NOT_BE_EMPTY" | "OUT_OF_RANGE" | "MUST_MATCH_PATTERN";
        description: string;
    },

    password: {
        field: "password";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_NOT_BE_EMPTY" | "OUT_OF_RANGE" | "MUST_MATCH_PATTERN";
        description: string;
    },
}
