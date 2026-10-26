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
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.errors[?(@.field == 'street')].title" contains "MUST_NOT_BE_EMPTY"
jsonpath "$.errors[?(@.field == 'town')].title" contains "REQUIRED"
jsonpath "$.errors[?(@.field == 'flat')].title" contains "UNKNOWN_FIELD"

POST {{base_url}}/addresses
X-CSRF-Token: {{csrf}}
{
    "town": "Moscow"
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'street')].title" contains "REQUIRED"
jsonpath "$.errors[?(@.field == 'town')].title" contains "INVALID_TYPE"

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
jsonpath "$.errors[?(@.field == 'town.x')].title" contains "MUST_NOT_BE_NULL"
jsonpath "$.errors[?(@.field == 'town.y')].title" contains "INVALID_TYPE"
jsonpath "$.errors[?(@.field == 'town.z')].title" contains "REQUIRED"
jsonpath "$.errors[?(@.field == 'town.w')].title" contains "UNKNOWN_FIELD"

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
jsonpath "$.errors[?(@.field == 'town.id')].title" contains "MUTUALLY_EXCLUSIVE"

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
jsonpath "$.errors[?(@.field == 'town.id')].title" contains "MUST_BE_POSITIVE"

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
jsonpath "$.status" == 404
jsonpath "$.type" == "/problems/location-not-found"
jsonpath "$.location_id" == 999

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
jsonpath "$.location_id" == 999

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
