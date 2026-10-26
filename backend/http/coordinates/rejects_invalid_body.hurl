GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "body_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "body_user",
    "password": "secret_1"
}
HTTP 200

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
{
    "x": -953,
    "y": -391
}
HTTP 201
[Captures]
id: jsonpath "$.id"

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
{
    "x": -954,
    "y": -391
}
HTTP 422
[Asserts]
jsonpath "$.status" == 422
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.errors[?(@.field == 'x')].title" contains "MUST_BE_GREATER_THAN_MINUS_954"
jsonpath "$.errors" count == 1

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
{
    "y": -392
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'x')].title" contains "REQUIRED"
jsonpath "$.errors[?(@.field == 'y')].title" contains "MUST_BE_GREATER_THAN_MINUS_392"

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
{
    "x": null,
    "y": "1"
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'x')].title" contains "MUST_NOT_BE_NULL"
jsonpath "$.errors[?(@.field == 'y')].title" contains "INVALID_TYPE"

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
{
    "x": 1.5,
    "y": 1
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'x')].title" contains "INVALID_TYPE"

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
{
    "x": 2147483648,
    "y": 9223372036854775808
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'x')].title" contains "OUT_OF_RANGE"
jsonpath "$.errors[?(@.field == 'y')].title" contains "OUT_OF_RANGE"

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
{
    "x": 1,
    "y": 1,
    "z": 1
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'z')].title" contains "UNKNOWN_FIELD"

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
Content-Type: application/json
`[1, 2]`
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == '$')].title" contains "INVALID_TYPE"

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
Content-Type: application/json
`null`
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == '$')].title" contains "MUST_NOT_BE_NULL"

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
Content-Type: application/json
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == '$')].title" contains "REQUIRED"

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
Content-Type: application/json
`{"x": 1,`
HTTP 400
[Asserts]
jsonpath "$.type" == "/problems/malformed-json"

PUT {{base_url}}/coordinates/{{id}}
X-CSRF-Token: {{csrf}}
{
    "x": -954,
    "y": 0
}
HTTP 422

GET {{base_url}}/coordinates/{{id}}
HTTP 200
[Asserts]
jsonpath "$.x" == -953
jsonpath "$.y" == -391

GET {{base_url}}/coordinates
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1
