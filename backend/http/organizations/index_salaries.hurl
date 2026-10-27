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
jsonpath "$.message" == "The X-CSRF-Token header is missing or does not match the current session."

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
jsonpath "$.message" == "Indexation would produce a salary outside the allowed range."

POST {{base_url}}/organizations/4/index_salaries
X-CSRF-Token: {{csrf}}
{
    "coefficient": 1e-100
}
HTTP 409
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.message" == "Indexation would produce a salary outside the allowed range."

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
jsonpath "$.message" == "Organization with id=999 does not exist."

POST {{base_url}}/organizations/abc/index_salaries
X-CSRF-Token: {{csrf}}
{
    "coefficient": 2
}
HTTP 400
[Asserts]
jsonpath "$.message" == "Parameter id must be an integer."

POST {{base_url}}/organizations/1/index_salaries
X-CSRF-Token: {{csrf}}
Content-Type: application/json
```
{"coefficient": 
```
HTTP 400
[Asserts]
jsonpath "$.message" == "The request body contains invalid JSON or a value of the wrong type."

POST {{base_url}}/organizations/1/index_salaries
X-CSRF-Token: {{csrf}}
{
    "coefficient": 0
}
HTTP 422
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.errors[0].field" == "coefficient"
jsonpath "$.errors[0].code" == "MUST_BE_POSITIVE"

POST {{base_url}}/organizations/1/index_salaries
X-CSRF-Token: {{csrf}}
{
    "coefficient": -1.5
}
HTTP 422
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.errors[0].field" == "coefficient"
jsonpath "$.errors[0].code" == "MUST_BE_POSITIVE"

POST {{base_url}}/organizations/1/index_salaries
X-CSRF-Token: {{csrf}}
{
    "coefficient": 1e-400
}
HTTP 422
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.errors[0].field" == "coefficient"
jsonpath "$.errors[0].code" == "MUST_BE_POSITIVE"

POST {{base_url}}/organizations/1/index_salaries
X-CSRF-Token: {{csrf}}
{
    "coefficient": 1e309
}
HTTP 422
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.errors[0].field" == "coefficient"
jsonpath "$.errors[0].code" == "OUT_OF_RANGE"

POST {{base_url}}/organizations/1/index_salaries
X-CSRF-Token: {{csrf}}
{
    "coefficient": null
}
HTTP 422
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.errors[0].field" == "coefficient"
jsonpath "$.errors[0].code" == "MUST_NOT_BE_NULL"

POST {{base_url}}/organizations/1/index_salaries
X-CSRF-Token: {{csrf}}
{
}
HTTP 422
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.errors[0].field" == "coefficient"
jsonpath "$.errors[0].code" == "MUST_NOT_BE_NULL"

POST {{base_url}}/organizations/1/index_salaries
X-CSRF-Token: {{csrf}}
{
    "coefficient": "1.1"
}
HTTP 400
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.message" == "The request body contains invalid JSON or a value of the wrong type."

GET {{base_url}}/workers/1
HTTP 200
[Asserts]
jsonpath "$.salary" == 100
