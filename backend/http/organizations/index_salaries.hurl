POST {{base_url}}/organizations/1/index_salaries
{
    "coefficient": 2
}
HTTP 401

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "salary_index_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "salary_index_user",
    "password": "secret_1"
}
HTTP 200

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/organizations/1/index_salaries
{
    "coefficient": 2
}
HTTP 403
[Asserts]
jsonpath "$.type" == "/problems/invalid-csrf-token"

GET {{base_url}}/workers/1
HTTP 200
[Asserts]
jsonpath "$.salary" == 100

POST {{base_url}}/organizations/1/index_salaries
X-CSRF-Token: {{csrf}}
{
    "coefficient": 2
}
HTTP 200
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.count" == 2

GET {{base_url}}/workers/1
HTTP 200
[Asserts]
jsonpath "$.salary" == 200

GET {{base_url}}/workers/2
HTTP 200
[Asserts]
jsonpath "$.salary" == 501

GET {{base_url}}/workers/3
HTTP 200
[Asserts]
jsonpath "$.salary" == 1000

POST {{base_url}}/organizations/1/index_salaries
X-CSRF-Token: {{csrf}}
{
    "coefficient": 0.5
}
HTTP 200
[Asserts]
jsonpath "$.count" == 2

GET {{base_url}}/workers/1
HTTP 200
[Asserts]
jsonpath "$.salary" == 100

GET {{base_url}}/workers/2
HTTP 200
[Asserts]
jsonpath "$.salary" == 250.5

POST {{base_url}}/organizations/3/index_salaries
X-CSRF-Token: {{csrf}}
{
    "coefficient": 1.1
}
HTTP 200
[Asserts]
jsonpath "$.count" == 0

POST {{base_url}}/organizations/4/index_salaries
X-CSRF-Token: {{csrf}}
{
    "coefficient": 10
}
HTTP 409
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/salary-indexation-conflict"
jsonpath "$.status" == 409
jsonpath "$.organization_id" == 4

POST {{base_url}}/organizations/4/index_salaries
X-CSRF-Token: {{csrf}}
{
    "coefficient": 1e-100
}
HTTP 409
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/salary-indexation-conflict"
jsonpath "$.status" == 409
jsonpath "$.organization_id" == 4

GET {{base_url}}/workers/4
HTTP 200
[Asserts]
jsonpath "$.salary" == 100

GET {{base_url}}/workers/5
HTTP 200
[Asserts]
jsonpath "$.salary" > 1000000000000000000

GET {{base_url}}/workers/6
HTTP 200
[Asserts]
jsonpath "$.salary" > 0

POST {{base_url}}/organizations/999/index_salaries
X-CSRF-Token: {{csrf}}
{
    "coefficient": 2
}
HTTP 404
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/organization-not-found"
jsonpath "$.status" == 404
jsonpath "$.organization_id" == 999

POST {{base_url}}/organizations/abc/index_salaries
X-CSRF-Token: {{csrf}}
{
    "coefficient": 2
}
HTTP 400
[Asserts]
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.errors[0].field" == "id"
jsonpath "$.errors[0].title" == "INVALID_TYPE"

POST {{base_url}}/organizations/1/index_salaries
X-CSRF-Token: {{csrf}}
Content-Type: application/json
```
{"coefficient": 
```
HTTP 400
[Asserts]
jsonpath "$.type" == "/problems/malformed-json"

POST {{base_url}}/organizations/1/index_salaries
X-CSRF-Token: {{csrf}}
{
    "coefficient": 0
}
HTTP 422
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.status" == 422
jsonpath "$.errors[0].field" == "coefficient"
jsonpath "$.errors[0].title" == "MUST_BE_POSITIVE"

POST {{base_url}}/organizations/1/index_salaries
X-CSRF-Token: {{csrf}}
{
    "coefficient": -1.5
}
HTTP 422
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.status" == 422
jsonpath "$.errors[0].field" == "coefficient"
jsonpath "$.errors[0].title" == "MUST_BE_POSITIVE"

POST {{base_url}}/organizations/1/index_salaries
X-CSRF-Token: {{csrf}}
{
    "coefficient": 1e-400
}
HTTP 422
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.status" == 422
jsonpath "$.errors[0].field" == "coefficient"
jsonpath "$.errors[0].title" == "MUST_BE_POSITIVE"

POST {{base_url}}/organizations/1/index_salaries
X-CSRF-Token: {{csrf}}
{
    "coefficient": 1e309
}
HTTP 422
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.status" == 422
jsonpath "$.errors[0].field" == "coefficient"
jsonpath "$.errors[0].title" == "OUT_OF_RANGE"

POST {{base_url}}/organizations/1/index_salaries
X-CSRF-Token: {{csrf}}
{
    "coefficient": null
}
HTTP 422
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.status" == 422
jsonpath "$.errors[0].field" == "coefficient"
jsonpath "$.errors[0].title" == "MUST_NOT_BE_NULL"

POST {{base_url}}/organizations/1/index_salaries
X-CSRF-Token: {{csrf}}
{
}
HTTP 422
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.status" == 422
jsonpath "$.errors[0].field" == "coefficient"
jsonpath "$.errors[0].title" == "REQUIRED"

POST {{base_url}}/organizations/1/index_salaries
X-CSRF-Token: {{csrf}}
{
    "coefficient": "1.1"
}
HTTP 422
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.status" == 422
jsonpath "$.errors[0].field" == "coefficient"
jsonpath "$.errors[0].title" == "INVALID_TYPE"

POST {{base_url}}/organizations/1/index_salaries
X-CSRF-Token: {{csrf}}
{
    "coefficient": 2,
    "status": "HIRED"
}
HTTP 422
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.status" == 422
jsonpath "$.errors[0].field" == "status"
jsonpath "$.errors[0].title" == "UNKNOWN_FIELD"

GET {{base_url}}/workers/1
HTTP 200
[Asserts]
jsonpath "$.salary" == 100
