GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
anonymous_session: cookie "JSESSIONID"
csrf: jsonpath "$.csrf_token"
[Asserts]
header "Content-Type" contains "application/json"
header "Cache-Control" contains "no-store"
cookie "JSESSIONID[HttpOnly]" exists
cookie "JSESSIONID[SameSite]" == "Lax"
cookie "JSESSIONID[Path]" == "/"
jsonpath "$.csrf_token" isString
jsonpath "$.csrf_token" != "{{anonymous_session}}"

GET {{base_url}}/auth/csrf
HTTP 200
[Asserts]
jsonpath "$.csrf_token" == "{{csrf}}"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "alice_flow",
    "password": "secret_1"
}
HTTP 201
[Captures]
user_id: jsonpath "$.id"
[Asserts]
jsonpath "$.id" isInteger
jsonpath "$.id" > 0
jsonpath "$.password" not exists
header "Set-Cookie" not exists

GET {{base_url}}/auth/session
HTTP 401

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "alice_flow",
    "password": "secret_1"
}
HTTP 200
[Asserts]
header "Cache-Control" contains "no-store"
cookie "JSESSIONID" != "{{anonymous_session}}"
cookie "JSESSIONID[HttpOnly]" exists
cookie "JSESSIONID[SameSite]" == "Lax"
jsonpath "$.id" == {{user_id}}
jsonpath "$.username" == "alice_flow"
jsonpath "$.password" not exists
jsonpath "$.password_hash" not exists

GET {{base_url}}/auth/session
HTTP 200
[Asserts]
header "Cache-Control" contains "no-store"
jsonpath "$.id" == {{user_id}}
jsonpath "$.username" == "alice_flow"

POST {{base_url}}/auth/logout
X-CSRF-Token: {{csrf}}
HTTP 403
[Asserts]
jsonpath "$.type" == "/problems/invalid-csrf-token"

GET {{base_url}}/auth/session
HTTP 200

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
authenticated_csrf: jsonpath "$.csrf_token"
[Asserts]
jsonpath "$.csrf_token" != "{{csrf}}"

POST {{base_url}}/auth/logout
X-CSRF-Token: {{authenticated_csrf}}
HTTP 204
[Asserts]
header "Cache-Control" contains "no-store"
header "Set-Cookie" contains "Max-Age=0"
body == ""

GET {{base_url}}/auth/session
HTTP 401
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.status" == 401
jsonpath "$.type" == "/problems/authentication-required"

POST {{base_url}}/auth/logout
X-CSRF-Token: {{authenticated_csrf}}
HTTP 403
[Asserts]
jsonpath "$.type" == "/problems/invalid-csrf-token"
