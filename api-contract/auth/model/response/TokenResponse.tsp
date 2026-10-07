namespace WorkersApi.Auth;

const tokenResponseExample: TokenResponse = #{
    access_token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIyMWU4MmU5MS0wZGI3LTRjMDYtODRlMi05MmUwODEwOGJlZjAiLCJqdGkiOiJsb2dpbi1leGFtcGxlIn0.example-signature",
    token_type: "Bearer",
    expires_in: 900
};

const refreshedTokenResponseExample: TokenResponse = #{
    ...tokenResponseExample,
    access_token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIyMWU4MmU5MS0wZGI3LTRjMDYtODRlMi05MmUwODEwOGJlZjAiLCJqdGkiOiJyZWZyZXNoLWV4YW1wbGUifQ.example-signature"
};

@doc("Access-токен сессии.")
@example(tokenResponseExample)
model TokenResponse {
    @doc("Access-токен JWT.")
    @minLength(1)
    access_token: string;

    @doc("Тип access-токена.")
    token_type: "Bearer";

    @doc("Срок действия access-токена в секундах.")
    @minValueExclusive(0)
    expires_in: int32;
}