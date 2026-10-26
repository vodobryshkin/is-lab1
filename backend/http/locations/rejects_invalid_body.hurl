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

POST {{base_url}}/locations
X-CSRF-Token: {{csrf}}
{
    "x": 1,
    "y": 1,
    "z": 1
}
HTTP 201
[Captures]
id: jsonpath "$.id"

POST {{base_url}}/locations
X-CSRF-Token: {{csrf}}
{
    "x": null,
    "y": 2147483648,
    "w": 1
}
HTTP 422
[Asserts]
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.errors[?(@.field == 'x')].title" contains "MUST_NOT_BE_NULL"
jsonpath "$.errors[?(@.field == 'y')].title" contains "OUT_OF_RANGE"
jsonpath "$.errors[?(@.field == 'z')].title" contains "REQUIRED"
jsonpath "$.errors[?(@.field == 'w')].title" contains "UNKNOWN_FIELD"

POST {{base_url}}/locations
X-CSRF-Token: {{csrf}}
{
    "x": "1",
    "y": 1,
    "z": 9223372036854775808
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'x')].title" contains "INVALID_TYPE"
jsonpath "$.errors[?(@.field == 'z')].title" contains "OUT_OF_RANGE"

POST {{base_url}}/locations
X-CSRF-Token: {{csrf}}
Content-Type: application/json
`"location"`
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == '$')].title" contains "INVALID_TYPE"

PUT {{base_url}}/locations/{{id}}
X-CSRF-Token: {{csrf}}
{
    "x": 5,
    "y": 5
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'z')].title" contains "REQUIRED"

GET {{base_url}}/locations/{{id}}
HTTP 200
[Asserts]
jsonpath "$.x" == 1
jsonpath "$.z" == 1

GET {{base_url}}/locations
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1
