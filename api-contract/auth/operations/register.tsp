import "@typespec/http";

import "../model/request/RegisterRequest.tsp";
import "../model/response/RegisterResponse.tsp";
import "../model/error/AuthValidationIssue.tsp";

import "../../common/error/ConflictError.tsp";
import "../../common/ok/CreatedResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Errors;
using WorkersApi.Auth.Errors;

namespace WorkersApi.Auth;

const registerSuccessOperationExample = #{
    parameters: #{ body: registerRequestExample },
    returnType: #{
        statusCode: 201,
        contentType: "application/json",
        body: registerResponseExample
    }
};

const registerUsernameConflictExample: ConflictError = #{
    statusCode: 409,
    contentType: "application/json",
    body: #{
        type: "/problems/username-already-exists",
        title: "Username already exists",
        status: 409,
        detail: "A user with this username already exists."
    }
};

const registerUsernameConflictOperationExample = #{
    returnType: registerUsernameConflictExample
};

@post
@route("/register")
@summary("Зарегистрироваться.")
@doc("Создаёт пользователя с уникальным логином.")
@opExample(registerSuccessOperationExample)
@opExample(authBadRequestOperationExample)
@opExample(registerUsernameConflictOperationExample)
@opExample(credentialsValidationOperationExample)
op register(
    @body body: RegisterRequest
):
    WorkersApi.Ok.CreatedResponse<RegisterResponse>
    | BadRequestError
    | ConflictError
    | ValidationError<422, AuthValidationIssue>;