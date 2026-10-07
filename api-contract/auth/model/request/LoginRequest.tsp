import "./RegisterRequest.tsp";

namespace WorkersApi.Auth;

const loginRequestExample: LoginRequest = registerRequestExample;

@doc("Данные входа.")
@example(loginRequestExample)
model LoginRequest {
    ...RegisterRequest;
}