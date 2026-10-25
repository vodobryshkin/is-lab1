GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "taken_name",
    "password": "secret_1"
}
HTTP 201
[Captures]
user_id: jsonpath "$.id"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "taken_name",
    "password": "other_pass"
}
HTTP 409
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.status" == 409
jsonpath "$.type" == "/problems/username-already-exists"

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "taken_name",
    "password": "other_pass"
}
HTTP 400

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "taken_name",
    "password": "secret_1"
}
HTTP 200
[Asserts]
jsonpath "$.id" == {{user_id}}
