POST {{base_url}}/workers/1/hire
{
    "organization_id": 2
}
HTTP 401

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "worker_hire_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "worker_hire_user",
    "password": "secret_1"
}
HTTP 200

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/workers/1/hire
{
    "organization_id": 2
}
HTTP 403
[Asserts]
jsonpath "$.type" == "/problems/invalid-csrf-token"

GET {{base_url}}/workers/1
HTTP 200
[Asserts]
jsonpath "$.organization.id" == 1
jsonpath "$.status" == "REGULAR"
jsonpath "$.rating" == 7

POST {{base_url}}/workers/1/hire
X-CSRF-Token: {{csrf}}
{
    "organization_id": 2
}
HTTP 204

GET {{base_url}}/workers/1
HTTP 200
[Asserts]
jsonpath "$.organization.id" == 2
jsonpath "$.status" == "HIRED"
jsonpath "$.name" == "Employed"
jsonpath "$.rating" == 7
jsonpath "$.start_date" == "2026-01-01T00:00:00Z"

POST {{base_url}}/workers/1/hire
X-CSRF-Token: {{csrf}}
{
    "organization_id": 2
}
HTTP 204

GET {{base_url}}/workers/1
HTTP 200
[Asserts]
jsonpath "$.organization.id" == 2
jsonpath "$.status" == "HIRED"

POST {{base_url}}/workers/2/hire
X-CSRF-Token: {{csrf}}
{
    "organization_id": 1
}
HTTP 204

GET {{base_url}}/workers/2
HTTP 200
[Asserts]
jsonpath "$.organization.id" == 1
jsonpath "$.status" == "HIRED"
jsonpath "$.rating" == 8

GET {{base_url}}/organizations/2
HTTP 200
[Asserts]
jsonpath "$.employees_count" == 3

POST {{base_url}}/workers/999/hire
X-CSRF-Token: {{csrf}}
{
    "organization_id": 999
}
HTTP 404
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/worker-not-found"
jsonpath "$.status" == 404
jsonpath "$.worker_id" == 999

POST {{base_url}}/workers/3/hire
X-CSRF-Token: {{csrf}}
{
    "organization_id": 999
}
HTTP 404
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/organization-not-found"
jsonpath "$.status" == 404
jsonpath "$.organization_id" == 999

GET {{base_url}}/workers/3
HTTP 200
[Asserts]
jsonpath "$.organization.id" == 1
jsonpath "$.status" == "REGULAR"
jsonpath "$.rating" == 9

POST {{base_url}}/workers/abc/hire
X-CSRF-Token: {{csrf}}
{
    "organization_id": 1
}
HTTP 400
[Asserts]
jsonpath "$.type" == "/problems/invalid-worker-id"

POST {{base_url}}/workers/3/hire
X-CSRF-Token: {{csrf}}
Content-Type: application/json
```
{"organization_id": 
```
HTTP 400
[Asserts]
jsonpath "$.type" == "/problems/malformed-json"

POST {{base_url}}/workers/3/hire
X-CSRF-Token: {{csrf}}
{
    "organization_id": 0
}
HTTP 422
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.status" == 422
jsonpath "$.errors[0].field" == "organization_id"
jsonpath "$.errors[0].title" == "MUST_BE_POSITIVE"

POST {{base_url}}/workers/3/hire
X-CSRF-Token: {{csrf}}
{
}
HTTP 422
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.status" == 422
jsonpath "$.errors[0].field" == "organization_id"
jsonpath "$.errors[0].title" == "REQUIRED"

POST {{base_url}}/workers/3/hire
X-CSRF-Token: {{csrf}}
{
    "organization_id": null
}
HTTP 422
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.status" == 422
jsonpath "$.errors[0].field" == "organization_id"
jsonpath "$.errors[0].title" == "MUST_NOT_BE_NULL"

POST {{base_url}}/workers/3/hire
X-CSRF-Token: {{csrf}}
{
    "organization_id": "2"
}
HTTP 422
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.status" == 422
jsonpath "$.errors[0].field" == "organization_id"
jsonpath "$.errors[0].title" == "INVALID_TYPE"

POST {{base_url}}/workers/3/hire
X-CSRF-Token: {{csrf}}
{
    "organization_id": 2,
    "status": "REGULAR"
}
HTTP 422
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.status" == 422
jsonpath "$.errors[0].field" == "status"
jsonpath "$.errors[0].title" == "UNKNOWN_FIELD"

GET {{base_url}}/workers/3
HTTP 200
[Asserts]
jsonpath "$.organization.id" == 1
jsonpath "$.status" == "REGULAR"
jsonpath "$.rating" == 9
