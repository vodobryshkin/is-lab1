import "@typespec/http";

import "../model/request/RegisterRequest.tsp";
import "../model/response/RegisterResponse.tsp";
import "../model/error/AuthValidationIssue.tsp";

import "../../common/error/ConflictError.tsp";
import "../../common/error/ForbiddenError.tsp";
import "../../common/ok/CreatedResponse.tsp";
import "../../common/security/CsrfTokenHeader.tsp";

using TypeSpec.Http;
using WorkersApi.Errors;
using WorkersApi.Security;
using WorkersApi.Auth.Errors;

namespace WorkersApi.Auth;

const registerSuccessOperationExample = #{
    parameters: #{ csrf_token: csrfTokenExample, body: registerRequestExample },
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
        message: "A user with this username already exists."
    }
};

const registerUsernameConflictOperationExample = #{
    returnType: registerUsernameConflictExample
};

@post
@route("/register")
@summary("Зарегистрироваться.")
@doc("""
    Создаёт пользователя с уникальным логином. Вход не выполняется: для работы
    нужно отдельно вызвать `POST /auth/login`. Требует CSRF-токен текущей сессии.
    """)
@returnsDoc("ID созданного пользователя.")
@opExample(registerSuccessOperationExample)
@opExample(authBadRequestOperationExample)
@opExample(forbiddenOperationExample)
@opExample(registerUsernameConflictOperationExample)
@opExample(credentialsValidationOperationExample)
op register(
    ...CsrfTokenHeader,
    @body body: RegisterRequest
):
    WorkersApi.Ok.CreatedResponse<RegisterResponse>
    | BadRequestError
    | ForbiddenError
    | ConflictError
    | ValidationError<AuthValidationIssue>;
