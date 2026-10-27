GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
Content-Type: application/json
`{"username":`
HTTP 400
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.message" == "The request body contains invalid JSON or a value of the wrong type."

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "password": "secret_1"
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'username')].code" contains "MUST_NOT_BE_NULL"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": null,
    "password": "secret_1"
}
HTTP 422
[Asserts]
jsonpath "$.errors" count == 1
jsonpath "$.errors[?(@.field == 'username')].code" contains "MUST_NOT_BE_NULL"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "number_password",
    "password": 123456
}
HTTP 400
[Asserts]
jsonpath "$.message" == "The request body contains invalid JSON or a value of the wrong type."

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "",
    "password": "secret_1"
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'username')].code" contains "MUST_NOT_BE_EMPTY"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "ab",
    "password": "12345"
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'username')].code" contains "OUT_OF_RANGE"
jsonpath "$.errors[?(@.field == 'password')].code" contains "OUT_OF_RANGE"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcde",
    "password": "abcdefghijabcdefghijabcdefghijabc"
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'username')].code" contains "OUT_OF_RANGE"
jsonpath "$.errors[?(@.field == 'password')].code" contains "OUT_OF_RANGE"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "bad name!",
    "password": "pass word"
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'username')].code" contains "MUST_MATCH_PATTERN"
jsonpath "$.errors[?(@.field == 'password')].code" contains "MUST_MATCH_PATTERN"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "abc",
    "password": "123456"
}
HTTP 201

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcd",
    "password": "abcdefghijabcdefghijabcdefghijab"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "abc",
    "password": "123456"
}
HTTP 200
