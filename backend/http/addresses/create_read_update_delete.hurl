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

POST {{base_url}}/addresses
X-CSRF-Token: {{csrf}}
{
    "street": "Ул. Пушкина",
    "town": {
        "x": 1,
        "y": 2,
        "z": 3
    }
}
HTTP 201
[Captures]
id: jsonpath "$.id"
[Asserts]
header "Location" matches "/api/v1/addresses/\\d+$"

GET {{base_url}}/addresses/{{id}}
HTTP 200
[Captures]
town_id: jsonpath "$.town.id"
[Asserts]
jsonpath "$.id" == {{id}}
jsonpath "$.street" == "Ул. Пушкина"
jsonpath "$.town.id" > 0
jsonpath "$.town.x" == 1
jsonpath "$.town.y" == 2
jsonpath "$.town.z" == 3

POST {{base_url}}/addresses
X-CSRF-Token: {{csrf}}
{
    "street": "Kronverksky",
    "town": {
        "id": {{town_id}}
    }
}
HTTP 201
[Captures]
second_id: jsonpath "$.id"

GET {{base_url}}/addresses/{{second_id}}
HTTP 200
[Asserts]
jsonpath "$.town.id" == {{town_id}}
jsonpath "$.town.z" == 3

POST {{base_url}}/addresses
X-CSRF-Token: {{csrf}}
{
    "street": "Без города",
    "town": null
}
HTTP 201
[Captures]
third_id: jsonpath "$.id"

GET {{base_url}}/addresses/{{third_id}}
HTTP 200
[Asserts]
jsonpath "$.town" == null

PUT {{base_url}}/addresses/{{id}}
X-CSRF-Token: {{csrf}}
{
    "street": "Новая улица",
    "town": null
}
HTTP 204

GET {{base_url}}/addresses/{{id}}
HTTP 200
[Asserts]
jsonpath "$.street" == "Новая улица"
jsonpath "$.town" == null

GET {{base_url}}/locations/{{town_id}}
HTTP 200

PUT {{base_url}}/addresses/{{id}}
X-CSRF-Token: {{csrf}}
{
    "street": "Новая улица",
    "town": {
        "x": 7,
        "y": 8,
        "z": 9
    }
}
HTTP 204

GET {{base_url}}/addresses/{{id}}
HTTP 200
[Asserts]
jsonpath "$.town.id" != {{town_id}}
jsonpath "$.town.x" == 7

DELETE {{base_url}}/addresses/{{second_id}}
X-CSRF-Token: {{csrf}}
HTTP 204

GET {{base_url}}/addresses/{{second_id}}
HTTP 404
[Asserts]
jsonpath "$.status" == 404
jsonpath "$.type" == "/problems/address-not-found"

GET {{base_url}}/locations/{{town_id}}
HTTP 200

DELETE {{base_url}}/addresses/{{second_id}}
X-CSRF-Token: {{csrf}}
HTTP 404
