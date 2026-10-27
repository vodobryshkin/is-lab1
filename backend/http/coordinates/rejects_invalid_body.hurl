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
jsonpath "$.errors[?(@.field == 'x')].code" contains "MUST_BE_GREATER_THAN_MINUS_954"
jsonpath "$.errors" count == 1
jsonpath "$.type" not exists
jsonpath "$.status" not exists
jsonpath "$.errors[0].field" == "x"
jsonpath "$.errors[0].message" == "x must be greater than -954."

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
{
    "y": -392
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'x')].code" contains "REQUIRED"
jsonpath "$.errors[?(@.field == 'y')].code" contains "MUST_BE_GREATER_THAN_MINUS_392"

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
{
    "x": null,
    "y": "1"
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'x')].code" contains "MUST_NOT_BE_NULL"
jsonpath "$.errors[?(@.field == 'y')].code" contains "INVALID_TYPE"

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
{
    "x": 1.5,
    "y": 1
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'x')].code" contains "INVALID_TYPE"

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
{
    "x": 2147483648,
    "y": 9223372036854775808
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'x')].code" contains "OUT_OF_RANGE"
jsonpath "$.errors[?(@.field == 'y')].code" contains "OUT_OF_RANGE"

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
{
    "x": 1,
    "y": 1,
    "z": 1
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'z')].code" contains "UNKNOWN_FIELD"

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
Content-Type: application/json
`[1, 2]`
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == '$')].code" contains "INVALID_TYPE"

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
Content-Type: application/json
`null`
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == '$')].code" contains "MUST_NOT_BE_NULL"

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
Content-Type: application/json
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == '$')].code" contains "REQUIRED"

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
Content-Type: application/json
`{"x": 1,`
HTTP 400
[Asserts]
jsonpath "$.message" == "The request body contains invalid JSON."

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
