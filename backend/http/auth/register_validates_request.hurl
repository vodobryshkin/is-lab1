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
jsonpath "$.status" == 400
jsonpath "$.type" == "/problems/malformed-json"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "password": "secret_1"
}
HTTP 422
[Asserts]
jsonpath "$.status" == 422
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.errors[?(@.field == 'username')].title" contains "REQUIRED"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": null,
    "password": 123456
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'username')].title" contains "MUST_NOT_BE_NULL"
jsonpath "$.errors[?(@.field == 'password')].title" contains "INVALID_TYPE"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "",
    "password": "secret_1"
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'username')].title" contains "MUST_NOT_BE_EMPTY"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "ab",
    "password": "12345"
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'username')].title" contains "OUT_OF_RANGE"
jsonpath "$.errors[?(@.field == 'password')].title" contains "OUT_OF_RANGE"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcde",
    "password": "abcdefghijabcdefghijabcdefghijabc"
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'username')].title" contains "OUT_OF_RANGE"
jsonpath "$.errors[?(@.field == 'password')].title" contains "OUT_OF_RANGE"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "bad name!",
    "password": "pass word"
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'username')].title" contains "MUST_MATCH_PATTERN"
jsonpath "$.errors[?(@.field == 'password')].title" contains "MUST_MATCH_PATTERN"

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
