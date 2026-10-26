GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "delete_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "delete_user",
    "password": "secret_1"
}
HTTP 200

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

DELETE {{base_url}}/coordinates/1
X-CSRF-Token: {{csrf}}
HTTP 409
[Asserts]
jsonpath "$.status" == 409
jsonpath "$.type" == "/problems/coordinates-are-in-use"
jsonpath "$.coordinates_id" == 1

GET {{base_url}}/coordinates/1
HTTP 200
[Asserts]
jsonpath "$.x" == 10

PUT {{base_url}}/coordinates/1
X-CSRF-Token: {{csrf}}
{
    "x": 11,
    "y": 21
}
HTTP 204

GET {{base_url}}/coordinates/1
HTTP 200
[Asserts]
jsonpath "$.x" == 11
jsonpath "$.y" == 21
