GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "wrong_pw_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "wrong_pw_user",
    "password": "secret_2"
}
HTTP 400
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.status" == 400
jsonpath "$.type" == "/problems/invalid-credentials"
jsonpath "$.detail" == "Username or password is incorrect."

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "missing_user",
    "password": "secret_1"
}
HTTP 400
[Asserts]
jsonpath "$.type" == "/problems/invalid-credentials"
jsonpath "$.detail" == "Username or password is incorrect."

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "WRONG_PW_USER",
    "password": "secret_1"
}
HTTP 400

GET {{base_url}}/auth/session
HTTP 401
