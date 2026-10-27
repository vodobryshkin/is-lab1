GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "crud_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "crud_user",
    "password": "secret_1"
}
HTTP 200

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
{
    "x": 10,
    "y": 20
}
HTTP 201
[Captures]
id: jsonpath "$.id"
[Asserts]
header "Location" matches "/api/v1/coordinates/\\d+$"
jsonpath "$.id" isInteger
jsonpath "$.id" > 0

GET {{base_url}}/coordinates/{{id}}
HTTP 200
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.id" == {{id}}
jsonpath "$.x" == 10
jsonpath "$.y" == 20

PUT {{base_url}}/coordinates/{{id}}
X-CSRF-Token: {{csrf}}
{
    "x": -953,
    "y": 9223372036854775807
}
HTTP 204
[Asserts]
body == ""

GET {{base_url}}/coordinates/{{id}}
HTTP 200
[Asserts]
jsonpath "$.x" == -953
jsonpath "$.y" == 9223372036854775807

DELETE {{base_url}}/coordinates/{{id}}
X-CSRF-Token: {{csrf}}
HTTP 204

GET {{base_url}}/coordinates/{{id}}
HTTP 404
[Asserts]
jsonpath "$.message" == "Coordinates with id={{id}} do not exist."

DELETE {{base_url}}/coordinates/{{id}}
X-CSRF-Token: {{csrf}}
HTTP 404

PUT {{base_url}}/coordinates/{{id}}
X-CSRF-Token: {{csrf}}
{
    "x": 1,
    "y": 1
}
HTTP 404
[Asserts]
jsonpath "$.message" == "Coordinates with id={{id}} do not exist."
