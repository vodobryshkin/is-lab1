namespace WorkersApi.Auth;

const currentUserResponseExample: CurrentUserResponse = #{
    id: 7,
    username: "vodobryshkin"
};

@doc("Публичные сведения о вошедшем пользователе.")
@example(currentUserResponseExample)
model CurrentUserResponse {
    @doc("ID пользователя.")
    @minValue(1)
    id: int64;

    @doc("Логин пользователя.")
    username: string;
}
