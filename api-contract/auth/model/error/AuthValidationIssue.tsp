import "../../../common/error/BadRequestError.tsp";
import "../../../common/error/ValidationError.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Auth.Errors;

const authValidationIssueExample = #{
    field: "password",
    code: "OUT_OF_RANGE",
    message: "Password must contain between 6 and 32 characters."
};

const authBadRequestExample: BadRequestError = #{
    statusCode: 400,
    contentType: "application/json",
    body: #{
        message: "The request body contains invalid JSON or a value of the wrong type."
    }
};

const credentialsValidationErrorExample:
    ValidationError<AuthValidationIssue> = #{
        statusCode: 422,
        contentType: "application/json",
        body: #{
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
        code: "MUST_NOT_BE_NULL" | "MUST_NOT_BE_EMPTY" | "OUT_OF_RANGE"
            | "MUST_MATCH_PATTERN";
        message: string;
    },

    password: {
        field: "password";
        code: "MUST_NOT_BE_NULL" | "MUST_NOT_BE_EMPTY" | "OUT_OF_RANGE"
            | "MUST_MATCH_PATTERN";
        message: string;
    },
}
