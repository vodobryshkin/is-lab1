GET {{base_url}}/addresses
HTTP 401

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "csrf_address_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "csrf_address_user",
    "password": "secret_1"
}
HTTP 200

POST {{base_url}}/addresses
{
    "street": "Kronverksky",
    "town": null
}
HTTP 403

GET {{base_url}}/addresses
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0
