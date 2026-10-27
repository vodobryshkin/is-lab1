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

POST {{base_url}}/locations
X-CSRF-Token: {{csrf}}
{
    "x": -2147483648,
    "y": 2147483647,
    "z": -9223372036854775808
}
HTTP 201
[Captures]
id: jsonpath "$.id"
[Asserts]
header "Location" matches "/api/v1/locations/\\d+$"
jsonpath "$.id" > 0

GET {{base_url}}/locations/{{id}}
HTTP 200
[Asserts]
jsonpath "$.id" == {{id}}
jsonpath "$.x" == -2147483648
jsonpath "$.y" == 2147483647
jsonpath "$.z" == -9223372036854775808

PUT {{base_url}}/locations/{{id}}
X-CSRF-Token: {{csrf}}
{
    "x": 1,
    "y": 2,
    "z": 3
}
HTTP 204

GET {{base_url}}/locations/{{id}}
HTTP 200
[Asserts]
jsonpath "$.x" == 1
jsonpath "$.y" == 2
jsonpath "$.z" == 3

DELETE {{base_url}}/locations/{{id}}
X-CSRF-Token: {{csrf}}
HTTP 204

GET {{base_url}}/locations/{{id}}
HTTP 404
[Asserts]
jsonpath "$.message" == "Location with id={{id}} does not exist."

PUT {{base_url}}/locations/{{id}}
X-CSRF-Token: {{csrf}}
{
    "x": 1,
    "y": 2,
    "z": 3
}
HTTP 404

DELETE {{base_url}}/locations/{{id}}
X-CSRF-Token: {{csrf}}
HTTP 404

GET {{base_url}}/locations/0
HTTP 400
[Asserts]
jsonpath "$.message" == "Parameter id must be greater than zero."
