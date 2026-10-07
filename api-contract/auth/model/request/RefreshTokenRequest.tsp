namespace WorkersApi.Auth;

const refreshTokenRequestExample: RefreshTokenRequest = #{
    refresh_token: "e134aff8-7826-45b0-9b72-a5daa36b56c5"
};

@doc("Refresh-токен сессии.")
@example(refreshTokenRequestExample)
model RefreshTokenRequest {
    @doc("Refresh-токен.")
    @secret
    @minLength(1)
    refresh_token: string;
}