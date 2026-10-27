GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "first_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "second_user",
    "password": "secret_2"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "first_user",
    "password": "secret_1"
}
HTTP 200

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
first_csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/login
X-CSRF-Token: {{first_csrf}}
{
    "username": "second_user",
    "password": "secret_2"
}
HTTP 200
[Asserts]
jsonpath "$.username" == "second_user"

GET {{base_url}}/auth/session
HTTP 200
[Asserts]
jsonpath "$.username" == "second_user"

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
second_csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/login
X-CSRF-Token: {{second_csrf}}
{
    "username": "first_user",
    "password": "wrong_password"
}
HTTP 400
[Asserts]
jsonpath "$.message" == "Username or password is incorrect."

GET {{base_url}}/auth/session
HTTP 401
