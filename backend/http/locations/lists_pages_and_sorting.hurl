GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "list_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "list_user",
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
    "x": 1,
    "y": 1,
    "z": 30
}
HTTP 201
[Captures]
first: jsonpath "$.id"

POST {{base_url}}/locations
X-CSRF-Token: {{csrf}}
{
    "x": 1,
    "y": 1,
    "z": -10
}
HTTP 201
[Captures]
second: jsonpath "$.id"

POST {{base_url}}/locations
X-CSRF-Token: {{csrf}}
{
    "x": 1,
    "y": 1,
    "z": 30
}
HTTP 201
[Captures]
third: jsonpath "$.id"

GET {{base_url}}/locations?sort_by=z&sort_order=desc&page_size=2
HTTP 200
[Asserts]
jsonpath "$.total_items" == 3
jsonpath "$.items" count == 2
jsonpath "$.items[0].id" == {{first}}
jsonpath "$.items[1].id" == {{third}}

GET {{base_url}}/locations?sort_by=z&sort_order=desc&page=2&page_size=2
HTTP 200
[Asserts]
jsonpath "$.items" count == 1
jsonpath "$.items[0].id" == {{second}}

GET {{base_url}}/locations?sort_by=z
HTTP 200
[Asserts]
jsonpath "$.items[0].id" == {{second}}
jsonpath "$.items[1].id" == {{first}}
jsonpath "$.items[2].id" == {{third}}

GET {{base_url}}/locations?sort_by=w
HTTP 400
[Asserts]
jsonpath "$.message" == "Parameter sort_by has an unsupported value."
