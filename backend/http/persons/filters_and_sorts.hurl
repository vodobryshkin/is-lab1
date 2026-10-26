GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "filter_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "filter_user",
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
    "eye_color": null,
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
first: jsonpath "$.id"

POST {{base_url}}/persons
X-CSRF-Token: {{csrf}}
{
    "eye_color": "BLUE",
    "hair_color": "BROWN",
    "location": {
        "x": 1,
        "y": 2,
        "z": 3
    },
    "birthday": "1990-01-01T00:00:00Z",
    "weight": 60,
    "nationality": "SPAIN"
}
HTTP 201
[Captures]
second: jsonpath "$.id"

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
    "birthday": "1980-01-01T00:00:00Z",
    "weight": 80,
    "nationality": "ITALY"
}
HTTP 201
[Captures]
third: jsonpath "$.id"

GET {{base_url}}/persons?eye_color=bro
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1
jsonpath "$.items[0].id" == {{third}}

GET {{base_url}}/persons?hair_color=bl
HTTP 200
[Asserts]
jsonpath "$.total_items" == 2
jsonpath "$.items[0].id" == {{first}}
jsonpath "$.items[1].id" == {{third}}

GET {{base_url}}/persons?hair_color=BL&nationality=ital
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1
jsonpath "$.items[0].id" == {{third}}

GET {{base_url}}/persons?eye_color=zzz
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0
jsonpath "$.items" count == 0

GET {{base_url}}/persons?nationality=
HTTP 200
[Asserts]
jsonpath "$.total_items" == 3

GET {{base_url}}/persons?sort_by=eye_color
HTTP 200
[Asserts]
jsonpath "$.items[0].id" == {{second}}
jsonpath "$.items[1].id" == {{third}}
jsonpath "$.items[2].id" == {{first}}

GET {{base_url}}/persons?sort_by=eye_color&sort_order=desc
HTTP 200
[Asserts]
jsonpath "$.items[0].id" == {{third}}
jsonpath "$.items[1].id" == {{second}}
jsonpath "$.items[2].id" == {{first}}

GET {{base_url}}/persons?sort_by=weight&sort_order=desc&page_size=2
HTTP 200
[Asserts]
jsonpath "$.total_items" == 3
jsonpath "$.items" count == 2
jsonpath "$.items[0].id" == {{third}}
jsonpath "$.items[1].id" == {{first}}

GET {{base_url}}/persons?sort_by=birthday
HTTP 200
[Asserts]
jsonpath "$.items[0].id" == {{third}}
jsonpath "$.items[2].id" == {{first}}

GET {{base_url}}/persons?sort_by=name
HTTP 400
[Asserts]
jsonpath "$.errors[?(@.field == 'sort_by')].title" contains "INVALID_ENUM_VALUE"
