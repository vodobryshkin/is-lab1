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

POST {{base_url}}/addresses
X-CSRF-Token: {{csrf}}
{
    "street": "Невский проспект",
    "town": null
}
HTTP 201

POST {{base_url}}/addresses
X-CSRF-Token: {{csrf}}
{
    "street": "невская улица",
    "town": null
}
HTTP 201

POST {{base_url}}/addresses
X-CSRF-Token: {{csrf}}
{
    "street": "100% street",
    "town": null
}
HTTP 201

POST {{base_url}}/addresses
X-CSRF-Token: {{csrf}}
{
    "street": "100x street",
    "town": null
}
HTTP 201

POST {{base_url}}/addresses
X-CSRF-Token: {{csrf}}
{
    "street": "a_b lane",
    "town": null
}
HTTP 201

POST {{base_url}}/addresses
X-CSRF-Token: {{csrf}}
{
    "street": "axb lane",
    "town": null
}
HTTP 201

GET {{base_url}}/addresses
[Query]
street: НЕВСК
HTTP 200
[Asserts]
jsonpath "$.total_items" == 2
jsonpath "$.items" count == 2

GET {{base_url}}/addresses
[Query]
street: %
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1
jsonpath "$.items[0].street" == "100% street"

GET {{base_url}}/addresses?street=a_b
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1
jsonpath "$.items[0].street" == "a_b lane"

GET {{base_url}}/addresses?street=
HTTP 200
[Asserts]
jsonpath "$.total_items" == 6

GET {{base_url}}/addresses?street=xyz
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0
jsonpath "$.items" count == 0

GET {{base_url}}/addresses?sort_by=street&street=lane
HTTP 200
[Asserts]
jsonpath "$.items[0].street" == "a_b lane"
jsonpath "$.items[1].street" == "axb lane"

GET {{base_url}}/addresses?sort_by=street&sort_order=desc&street=lane
HTTP 200
[Asserts]
jsonpath "$.items" count == 2
jsonpath "$.items[0].street" == "axb lane"
jsonpath "$.items[1].street" == "a_b lane"

GET {{base_url}}/addresses
[Query]
sort_by: street
sort_order: desc
street: невск
HTTP 200
[Asserts]
jsonpath "$.items[0].street" == "Невский проспект"
jsonpath "$.items[1].street" == "невская улица"

GET {{base_url}}/addresses?street=street&page=2&page_size=1
HTTP 200
[Asserts]
jsonpath "$.total_items" == 2
jsonpath "$.items" count == 1
jsonpath "$.items[0].street" == "100x street"
