namespace WorkersApi.Auth;

const registerResponseExample: RegisterResponse = #{
    id: "21e82e91-0db7-4c06-84e2-92e08108bef0"
};

@doc("ID созданного пользователя.")
@example(registerResponseExample)
model RegisterResponse {
    @doc("ID пользователя.")
    @format("uuid")
    id: string;
}