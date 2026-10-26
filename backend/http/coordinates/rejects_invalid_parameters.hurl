GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "params_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "params_user",
    "password": "secret_1"
}
HTTP 200

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

GET {{base_url}}/coordinates/0
HTTP 400
[Asserts]
jsonpath "$.status" == 400
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.title" == "Invalid request parameters"
jsonpath "$.errors[?(@.field == 'id')].title" contains "MUST_BE_POSITIVE"

GET {{base_url}}/coordinates/abc
HTTP 400
[Asserts]
jsonpath "$.errors[?(@.field == 'id')].title" contains "INVALID_TYPE"

DELETE {{base_url}}/coordinates/99999999999999999999
X-CSRF-Token: {{csrf}}
HTTP 400
[Asserts]
jsonpath "$.errors[?(@.field == 'id')].title" contains "OUT_OF_RANGE"

GET {{base_url}}/coordinates/999
HTTP 404
[Asserts]
jsonpath "$.coordinates_id" == 999

GET {{base_url}}/coordinates?page=0&page_size=101&sort_by=z&sort_order=up
HTTP 400
[Asserts]
jsonpath "$.errors" count == 4
jsonpath "$.errors[?(@.field == 'page')].title" contains "MUST_BE_POSITIVE"
jsonpath "$.errors[?(@.field == 'page_size')].title" contains "OUT_OF_RANGE"
jsonpath "$.errors[?(@.field == 'sort_by')].title" contains "INVALID_ENUM_VALUE"
jsonpath "$.errors[?(@.field == 'sort_order')].title" contains "INVALID_ENUM_VALUE"

GET {{base_url}}/coordinates?page=abc&page_size=0
HTTP 400
[Asserts]
jsonpath "$.errors[?(@.field == 'page')].title" contains "INVALID_TYPE"
jsonpath "$.errors[?(@.field == 'page_size')].title" contains "MUST_BE_POSITIVE"

GET {{base_url}}/coordinates?page=2147483648
HTTP 400
[Asserts]
jsonpath "$.errors[?(@.field == 'page')].title" contains "OUT_OF_RANGE"
