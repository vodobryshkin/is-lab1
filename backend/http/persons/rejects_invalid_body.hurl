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

POST {{base_url}}/persons
X-CSRF-Token: {{csrf}}
{
    "eye_color": "BROWN",
    "hair_color": "BLACK",
    "location": {
        "x": 1,
        "y": 2,
        "z": 3
    },
    "birthday": "2000-01-01T00:00:00Z",
    "weight": 70,
    "nationality": "RUSSIA"
}
HTTP 201
[Captures]
id: jsonpath "$.id"

POST {{base_url}}/persons
X-CSRF-Token: {{csrf}}
{
    "hair_color": null,
    "location": null,
    "birthday": "yesterday",
    "weight": 0,
    "nationality": "FRANCE",
    "age": 30
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'eye_color')].code" contains "REQUIRED"
jsonpath "$.errors[?(@.field == 'hair_color')].code" contains "MUST_NOT_BE_NULL"
jsonpath "$.errors[?(@.field == 'location')].code" contains "MUST_NOT_BE_NULL"
jsonpath "$.errors[?(@.field == 'birthday')].code" contains "INVALID_DATE_TIME"
jsonpath "$.errors[?(@.field == 'weight')].code" contains "MUST_BE_POSITIVE"
jsonpath "$.errors[?(@.field == 'nationality')].code" contains "INVALID_ENUM_VALUE"
jsonpath "$.errors[?(@.field == 'age')].code" contains "UNKNOWN_FIELD"
jsonpath "$.errors" count == 7

POST {{base_url}}/persons
X-CSRF-Token: {{csrf}}
{
    "eye_color": "PINK",
    "hair_color": 1,
    "location": {
        "x": 1
    },
    "birthday": "2000-05-06T10:08:09",
    "weight": 1e39,
    "nationality": "RUSSIA"
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'eye_color')].code" contains "INVALID_ENUM_VALUE"
jsonpath "$.errors[?(@.field == 'hair_color')].code" contains "INVALID_TYPE"
jsonpath "$.errors[?(@.field == 'location.y')].code" contains "REQUIRED"
jsonpath "$.errors[?(@.field == 'location.z')].code" contains "REQUIRED"
jsonpath "$.errors[?(@.field == 'birthday')].code" contains "INVALID_DATE_TIME"
jsonpath "$.errors[?(@.field == 'weight')].code" contains "OUT_OF_RANGE"

POST {{base_url}}/persons
X-CSRF-Token: {{csrf}}
{
    "eye_color": null,
    "hair_color": "BLACK",
    "location": {
        "id": 1,
        "z": 1
    },
    "birthday": "2000-01-01T00:00:00Z",
    "weight": -1,
    "nationality": "RUSSIA"
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'location.id')].code" contains "MUTUALLY_EXCLUSIVE"
jsonpath "$.errors[?(@.field == 'weight')].code" contains "MUST_BE_POSITIVE"

POST {{base_url}}/persons
X-CSRF-Token: {{csrf}}
{
    "eye_color": null,
    "hair_color": "BLACK",
    "location": {
        "id": 999
    },
    "birthday": "2000-01-01T00:00:00Z",
    "weight": 70,
    "nationality": "RUSSIA"
}
HTTP 404
[Asserts]
jsonpath "$.message" == "Location with id=999 does not exist."

PUT {{base_url}}/persons/{{id}}
X-CSRF-Token: {{csrf}}
{
    "eye_color": null,
    "hair_color": "WHITE",
    "location": {
        "id": 999
    },
    "birthday": "2000-01-01T00:00:00Z",
    "weight": 99,
    "nationality": "SPAIN"
}
HTTP 404
[Asserts]
jsonpath "$.message" == "Location with id=999 does not exist."

GET {{base_url}}/persons/{{id}}
HTTP 200
[Asserts]
jsonpath "$.eye_color" == "BROWN"
jsonpath "$.hair_color" == "BLACK"
jsonpath "$.weight" == 70

GET {{base_url}}/persons
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1

GET {{base_url}}/locations
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1
