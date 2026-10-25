import "@typespec/http";

import "../model/response/CurrentUserResponse.tsp";

import "../../common/error/UnauthorizedError.tsp";
import "../../common/ok/OkResponse.tsp";

using TypeSpec.Http;
using WorkersApi.Errors;

namespace WorkersApi.Auth;

const getSessionSuccessOperationExample = #{
    returnType: #{
        statusCode: 200,
        contentType: "application/json",
        cache_control: "no-store",
        body: currentUserResponseExample
    }
};

@get
@route("/session")
@useAuth(WorkersApi.Security.SessionCookieAuth)
@summary("Получить текущего пользователя.")
@doc("Возвращает пользователя текущей сессии. Без входа возвращает 401.")
@returnsDoc("Вошедший пользователь.")
@opExample(getSessionSuccessOperationExample)
@opExample(unauthorizedOperationExample)
op getSession():
    {
        ...WorkersApi.Ok.OkResponse<CurrentUserResponse>;

        @header("Cache-Control")
        cache_control: "no-store";
    }
    | UnauthorizedError;
