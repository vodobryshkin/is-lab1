GET {{base_url}}/coordinates
HTTP 401
[Asserts]
jsonpath "$.message" == "Log in to access this resource."

POST {{base_url}}/coordinates
{
    "x": 1,
    "y": 1
}
HTTP 401

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "csrf_coord_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "csrf_coord_user",
    "password": "secret_1"
}
HTTP 200

POST {{base_url}}/coordinates
{
    "x": 1,
    "y": 1
}
HTTP 403
[Asserts]
jsonpath "$.message" == "The X-CSRF-Token header is missing or does not match the current session."

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
{
    "x": 1,
    "y": 1
}
HTTP 403

GET {{base_url}}/coordinates
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0
