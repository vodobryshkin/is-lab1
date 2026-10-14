import "../../../common/model/IdResponse.tsp";

using WorkersApi.Ok;

namespace WorkersApi.Auth;

const registerResponseExample: RegisterResponse = #{
    ...idResponseExample
};

@doc("ID созданного пользователя.")
model RegisterResponse is IdResponse;
