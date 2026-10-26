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

POST {{base_url}}/organizations
X-CSRF-Token: {{csrf}}
{
    "full_name": "Base",
    "annual_turnover": 1,
    "employees_count": 3,
    "rating": 1,
    "type": null,
    "official_address": {
        "street": "Base Official",
        "town": null
    },
    "postal_address": {
        "street": "Base Postal",
        "town": null
    }
}
HTTP 201
[Captures]
id: jsonpath "$.id"

GET {{base_url}}/organizations/{{id}}
HTTP 200
[Captures]
official_id: jsonpath "$.official_address.id"

POST {{base_url}}/organizations
X-CSRF-Token: {{csrf}}
{
    "full_name": " ",
    "annual_turnover": 0,
    "employees_count": 0,
    "rating": "5",
    "type": "STATE",
    "official_address": {
        "street": "A",
        "town": null
    },
    "official_address_id": 1,
    "flag": true
}
HTTP 422
[Asserts]
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.errors[?(@.field == 'full_name')].title" contains "MUST_NOT_BE_EMPTY"
jsonpath "$.errors[?(@.field == 'annual_turnover')].title" contains "MUST_BE_POSITIVE"
jsonpath "$.errors[?(@.field == 'employees_count')].title" contains "MUST_BE_POSITIVE"
jsonpath "$.errors[?(@.field == 'rating')].title" contains "INVALID_TYPE"
jsonpath "$.errors[?(@.field == 'type')].title" contains "INVALID_ENUM_VALUE"
jsonpath "$.errors[?(@.field == 'official_address')].title" contains "MUTUALLY_EXCLUSIVE"
jsonpath "$.errors[?(@.field == 'official_address_id')].title" contains "MUTUALLY_EXCLUSIVE"
jsonpath "$.errors[?(@.field == 'postal_address')].title" contains "REQUIRED"
jsonpath "$.errors[?(@.field == 'flag')].title" contains "UNKNOWN_FIELD"
jsonpath "$.errors" count == 9

POST {{base_url}}/organizations
X-CSRF-Token: {{csrf}}
{
    "full_name": "Nested",
    "annual_turnover": 1e39,
    "employees_count": 1,
    "rating": 1,
    "type": null,
    "official_address": {
        "street": "",
        "town": {
            "x": null,
            "y": 1,
            "z": 1
        }
    },
    "postal_address_id": 0
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'annual_turnover')].title" contains "OUT_OF_RANGE"
jsonpath "$.errors[?(@.field == 'official_address.street')].title" contains "MUST_NOT_BE_EMPTY"
jsonpath "$.errors[?(@.field == 'official_address.town.x')].title" contains "MUST_NOT_BE_NULL"
jsonpath "$.errors[?(@.field == 'postal_address_id')].title" contains "MUST_BE_POSITIVE"

POST {{base_url}}/organizations
X-CSRF-Token: {{csrf}}
{
    "full_name": "Lost Address",
    "annual_turnover": 1,
    "employees_count": 1,
    "rating": 1,
    "type": null,
    "official_address": {
        "street": "Would Be Created",
        "town": {
            "x": 9,
            "y": 9,
            "z": 9
        }
    },
    "postal_address_id": 999
}
HTTP 404
[Asserts]
jsonpath "$.status" == 404
jsonpath "$.type" == "/problems/address-not-found"
jsonpath "$.address_id" not exists

POST {{base_url}}/organizations
X-CSRF-Token: {{csrf}}
{
    "full_name": "Lost Location",
    "annual_turnover": 1,
    "employees_count": 1,
    "rating": 1,
    "type": null,
    "official_address_id": {{official_id}},
    "postal_address": {
        "street": "Would Be Created",
        "town": {
            "id": 999
        }
    }
}
HTTP 404
[Asserts]
jsonpath "$.type" == "/problems/location-not-found"
jsonpath "$.location_id" == 999

PUT {{base_url}}/organizations/{{id}}
X-CSRF-Token: {{csrf}}
{
    "full_name": "Changed",
    "annual_turnover": 2,
    "employees_count": 2,
    "rating": 2,
    "type": "TRUST",
    "official_address": {
        "street": "Would Be Created",
        "town": null
    },
    "postal_address_id": 999
}
HTTP 404
[Asserts]
jsonpath "$.type" == "/problems/address-not-found"

GET {{base_url}}/organizations/{{id}}
HTTP 200
[Asserts]
jsonpath "$.full_name" == "Base"
jsonpath "$.type" == null

GET {{base_url}}/organizations
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1

GET {{base_url}}/addresses
HTTP 200
[Asserts]
jsonpath "$.total_items" == 2

GET {{base_url}}/locations
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0
