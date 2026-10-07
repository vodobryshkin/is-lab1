namespace WorkersApi.Auth;

const registerRequestExample: RegisterRequest = #{
    username: "vodobryshkin",
    password: "Vovan123"
};

@doc("Данные регистрации.")
@example(registerRequestExample)
model RegisterRequest {
    @doc("Логин пользователя.")
    @minLength(1)
    username: string;

    @doc("Пароль пользователя.")
    @secret
    @minLength(1)
    password: string;
}