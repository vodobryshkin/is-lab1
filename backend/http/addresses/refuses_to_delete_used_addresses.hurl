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

DELETE {{base_url}}/addresses/1
X-CSRF-Token: {{csrf}}
HTTP 409
[Asserts]
jsonpath "$.status" == 409
jsonpath "$.type" == "/problems/address-is-in-use"
jsonpath "$.address_id" == 1

DELETE {{base_url}}/addresses/2
X-CSRF-Token: {{csrf}}
HTTP 409
[Asserts]
jsonpath "$.address_id" == 2

DELETE {{base_url}}/addresses/3
X-CSRF-Token: {{csrf}}
HTTP 204

GET {{base_url}}/addresses
HTTP 200
[Asserts]
jsonpath "$.total_items" == 2
