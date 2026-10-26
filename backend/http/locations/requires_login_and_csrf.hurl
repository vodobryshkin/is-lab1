GET {{base_url}}/locations
HTTP 401

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "csrf_location_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "csrf_location_user",
    "password": "secret_1"
}
HTTP 200

POST {{base_url}}/locations
{
    "x": 1,
    "y": 1,
    "z": 1
}
HTTP 403
[Asserts]
jsonpath "$.type" == "/problems/invalid-csrf-token"

GET {{base_url}}/locations
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0
