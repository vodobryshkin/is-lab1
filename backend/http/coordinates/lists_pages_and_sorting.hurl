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

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
{
    "x": 5,
    "y": 1
}
HTTP 201
[Captures]
first: jsonpath "$.id"

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
{
    "x": -1,
    "y": 2
}
HTTP 201
[Captures]
second: jsonpath "$.id"

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
{
    "x": 5,
    "y": 3
}
HTTP 201
[Captures]
third: jsonpath "$.id"

GET {{base_url}}/coordinates
HTTP 200
[Asserts]
jsonpath "$.page" == 1
jsonpath "$.page_size" == 20
jsonpath "$.total_items" == 3
jsonpath "$.items" count == 3
jsonpath "$.items[0].id" == {{first}}
jsonpath "$.items[2].id" == {{third}}

GET {{base_url}}/coordinates?page_size=2
HTTP 200
[Asserts]
jsonpath "$.page_size" == 2
jsonpath "$.total_items" == 3
jsonpath "$.items" count == 2
jsonpath "$.items[0].id" == {{first}}
jsonpath "$.items[1].id" == {{second}}

GET {{base_url}}/coordinates?page=2&page_size=2
HTTP 200
[Asserts]
jsonpath "$.page" == 2
jsonpath "$.items" count == 1
jsonpath "$.items[0].id" == {{third}}

GET {{base_url}}/coordinates?page=3&page_size=2
HTTP 200
[Asserts]
jsonpath "$.items" count == 0
jsonpath "$.total_items" == 3

GET {{base_url}}/coordinates?page=2147483647&page_size=100
HTTP 200
[Asserts]
jsonpath "$.items" count == 0

GET {{base_url}}/coordinates?sort_by=x&sort_order=desc
HTTP 200
[Asserts]
jsonpath "$.items[0].id" == {{first}}
jsonpath "$.items[1].id" == {{third}}
jsonpath "$.items[2].id" == {{second}}

GET {{base_url}}/coordinates?sort_by=y&sort_order=desc
HTTP 200
[Asserts]
jsonpath "$.items[0].id" == {{third}}
jsonpath "$.items[2].id" == {{first}}

GET {{base_url}}/coordinates?sort_by=x
HTTP 200
[Asserts]
jsonpath "$.items[0].id" == {{second}}
jsonpath "$.items[1].id" == {{first}}
jsonpath "$.items[2].id" == {{third}}
