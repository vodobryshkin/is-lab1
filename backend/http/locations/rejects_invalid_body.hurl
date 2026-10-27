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
    "w": 1
}
HTTP 422
[Asserts]
jsonpath "$.errors" count == 3
jsonpath "$.errors[?(@.field == 'x')].code" contains "MUST_NOT_BE_NULL"
jsonpath "$.errors[?(@.field == 'y')].code" contains "MUST_NOT_BE_NULL"
jsonpath "$.errors[?(@.field == 'z')].code" contains "MUST_NOT_BE_NULL"

POST {{base_url}}/locations
X-CSRF-Token: {{csrf}}
{
    "x": 1,
    "y": 2147483648,
    "z": 1
}
HTTP 400
[Asserts]
jsonpath "$.message" == "The request body contains invalid JSON or a value of the wrong type."

POST {{base_url}}/locations
X-CSRF-Token: {{csrf}}
{
    "x": "1",
    "y": 1,
    "z": 9223372036854775808
}
HTTP 400
[Asserts]
jsonpath "$.message" == "The request body contains invalid JSON or a value of the wrong type."

POST {{base_url}}/locations
X-CSRF-Token: {{csrf}}
Content-Type: application/json
`"location"`
HTTP 400
[Asserts]
jsonpath "$.message" == "The request body contains invalid JSON or a value of the wrong type."

PUT {{base_url}}/locations/{{id}}
X-CSRF-Token: {{csrf}}
{
    "x": 5,
    "y": 5
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'z')].code" contains "MUST_NOT_BE_NULL"

GET {{base_url}}/locations/{{id}}
HTTP 200
[Asserts]
jsonpath "$.x" == 1
jsonpath "$.z" == 1

GET {{base_url}}/locations
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1
