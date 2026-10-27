GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "body_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "body_user",
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
    "street": "Kronverksky",
    "town": null
}
HTTP 201
[Captures]
id: jsonpath "$.id"

POST {{base_url}}/addresses
X-CSRF-Token: {{csrf}}
{
    "street": "   ",
    "flat": 1
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'street')].code" contains "MUST_NOT_BE_EMPTY"
jsonpath "$.errors[?(@.field == 'town')].code" contains "REQUIRED"
jsonpath "$.errors[?(@.field == 'flat')].code" contains "UNKNOWN_FIELD"

POST {{base_url}}/addresses
X-CSRF-Token: {{csrf}}
{
    "town": "Moscow"
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'street')].code" contains "REQUIRED"
jsonpath "$.errors[?(@.field == 'town')].code" contains "INVALID_TYPE"

POST {{base_url}}/addresses
X-CSRF-Token: {{csrf}}
{
    "street": "Kronverksky",
    "town": {
        "x": null,
        "y": "2",
        "w": 1
    }
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'town.x')].code" contains "MUST_NOT_BE_NULL"
jsonpath "$.errors[?(@.field == 'town.y')].code" contains "INVALID_TYPE"
jsonpath "$.errors[?(@.field == 'town.z')].code" contains "REQUIRED"
jsonpath "$.errors[?(@.field == 'town.w')].code" contains "UNKNOWN_FIELD"

POST {{base_url}}/addresses
X-CSRF-Token: {{csrf}}
{
    "street": "Kronverksky",
    "town": {
        "id": 1,
        "x": 1
    }
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'town.id')].code" contains "MUTUALLY_EXCLUSIVE"

POST {{base_url}}/addresses
X-CSRF-Token: {{csrf}}
{
    "street": "Kronverksky",
    "town": {
        "id": 0
    }
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'town.id')].code" contains "MUST_BE_POSITIVE"

POST {{base_url}}/addresses
X-CSRF-Token: {{csrf}}
{
    "street": "Kronverksky",
    "town": {
        "id": 999
    }
}
HTTP 404
[Asserts]
jsonpath "$.message" == "Location with id=999 does not exist."

PUT {{base_url}}/addresses/{{id}}
X-CSRF-Token: {{csrf}}
{
    "street": "Изменённая",
    "town": {
        "id": 999
    }
}
HTTP 404
[Asserts]
jsonpath "$.message" == "Location with id=999 does not exist."

GET {{base_url}}/addresses/{{id}}
HTTP 200
[Asserts]
jsonpath "$.street" == "Kronverksky"
jsonpath "$.town" == null

GET {{base_url}}/addresses
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1

GET {{base_url}}/locations
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0
