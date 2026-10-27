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
jsonpath "$.message" == "Parameter id must be greater than zero."

GET {{base_url}}/coordinates/abc
HTTP 400
[Asserts]
jsonpath "$.message" == "Parameter id must be an integer."

DELETE {{base_url}}/coordinates/99999999999999999999
X-CSRF-Token: {{csrf}}
HTTP 400
[Asserts]
jsonpath "$.message" == "Parameter id must not exceed 9223372036854775807."

GET {{base_url}}/coordinates/999
HTTP 404
[Asserts]
jsonpath "$.message" == "Coordinates with id=999 do not exist."
jsonpath "$.type" not exists
jsonpath "$.status" not exists
jsonpath "$.coordinates_id" not exists

GET {{base_url}}/coordinates?sort_by=z
HTTP 400
[Asserts]
jsonpath "$.message" == "Parameter sort_by has an unsupported value."

GET {{base_url}}/coordinates?sort_order=up
HTTP 400
[Asserts]
jsonpath "$.message" == "Parameter sort_order must be asc or desc."

GET {{base_url}}/coordinates?page=0
HTTP 400
[Asserts]
jsonpath "$.message" == "Parameter page must be greater than zero."

GET {{base_url}}/coordinates?page_size=101
HTTP 400
[Asserts]
jsonpath "$.message" == "Parameter page_size must not exceed 100."

GET {{base_url}}/coordinates?page=abc
HTTP 400
[Asserts]
jsonpath "$.message" == "Parameter page must be an integer."

GET {{base_url}}/coordinates?page_size=0
HTTP 400
[Asserts]
jsonpath "$.message" == "Parameter page_size must be greater than zero."

GET {{base_url}}/coordinates?page=2147483648
HTTP 400
[Asserts]
jsonpath "$.message" == "Parameter page must not exceed 2147483647."
