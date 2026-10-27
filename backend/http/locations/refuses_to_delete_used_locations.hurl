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

DELETE {{base_url}}/locations/1
X-CSRF-Token: {{csrf}}
HTTP 409
[Asserts]
jsonpath "$.message" == "The location cannot be deleted while addresses or persons reference it."

DELETE {{base_url}}/locations/2
X-CSRF-Token: {{csrf}}
HTTP 409
[Asserts]
jsonpath "$.message" == "The location cannot be deleted while addresses or persons reference it."

PUT {{base_url}}/locations/2
X-CSRF-Token: {{csrf}}
{
    "x": 20,
    "y": 20,
    "z": 20
}
HTTP 204

GET {{base_url}}/locations/2
HTTP 200
[Asserts]
jsonpath "$.z" == 20

DELETE {{base_url}}/locations/3
X-CSRF-Token: {{csrf}}
HTTP 204

GET {{base_url}}/locations
HTTP 200
[Asserts]
jsonpath "$.total_items" == 2
