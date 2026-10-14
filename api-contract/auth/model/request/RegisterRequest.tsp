namespace WorkersApi.Auth;

const registerRequestExample: RegisterRequest = #{
    username: "vodobryshkin",
    password: "Vovan123"
};

@doc("Данные регистрации.")
@example(registerRequestExample)
model RegisterRequest {
    @doc("Уникальный логин пользователя.")
    @minLength(3)
    @maxLength(64)
    @pattern("^[a-zA-Z0-9_]{3,64}")
    username: string;

    @doc("Пароль пользователя.")
    @secret
    @minLength(6)
    @maxLength(255)
    @pattern("^[a-zA-Z0-9_]{6,32}")
    password: string;
}
