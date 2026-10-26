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
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.errors[?(@.field == 'eye_color')].title" contains "REQUIRED"
jsonpath "$.errors[?(@.field == 'hair_color')].title" contains "MUST_NOT_BE_NULL"
jsonpath "$.errors[?(@.field == 'location')].title" contains "MUST_NOT_BE_NULL"
jsonpath "$.errors[?(@.field == 'birthday')].title" contains "INVALID_DATE_TIME"
jsonpath "$.errors[?(@.field == 'weight')].title" contains "MUST_BE_POSITIVE"
jsonpath "$.errors[?(@.field == 'nationality')].title" contains "INVALID_ENUM_VALUE"
jsonpath "$.errors[?(@.field == 'age')].title" contains "UNKNOWN_FIELD"
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
jsonpath "$.errors[?(@.field == 'eye_color')].title" contains "INVALID_ENUM_VALUE"
jsonpath "$.errors[?(@.field == 'hair_color')].title" contains "INVALID_TYPE"
jsonpath "$.errors[?(@.field == 'location.y')].title" contains "REQUIRED"
jsonpath "$.errors[?(@.field == 'location.z')].title" contains "REQUIRED"
jsonpath "$.errors[?(@.field == 'birthday')].title" contains "INVALID_DATE_TIME"
jsonpath "$.errors[?(@.field == 'weight')].title" contains "OUT_OF_RANGE"

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
jsonpath "$.errors[?(@.field == 'location.id')].title" contains "MUTUALLY_EXCLUSIVE"
jsonpath "$.errors[?(@.field == 'weight')].title" contains "MUST_BE_POSITIVE"

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
jsonpath "$.type" == "/problems/location-not-found"
jsonpath "$.location_id" == 999

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
jsonpath "$.location_id" == 999

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
