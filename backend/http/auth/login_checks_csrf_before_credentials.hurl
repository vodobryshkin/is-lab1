GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "csrf_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
{
    "username": "csrf_user",
    "password": "secret_1"
}
HTTP 403
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.status" == 403
jsonpath "$.type" == "/problems/invalid-csrf-token"

POST {{base_url}}/auth/login
X-CSRF-Token: wrong-token
{
    "username": "csrf_user",
    "password": "secret_1"
}
HTTP 403
[Asserts]
jsonpath "$.type" == "/problems/invalid-csrf-token"

GET {{base_url}}/auth/session
HTTP 401

POST {{base_url}}/auth/register
X-CSRF-Token: wrong-token
{
    "username": "csrf_other",
    "password": "secret_1"
}
HTTP 403

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "csrf_other",
    "password": "secret_1"
}
HTTP 400
[Asserts]
jsonpath "$.type" == "/problems/invalid-credentials"
