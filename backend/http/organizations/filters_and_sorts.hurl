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

POST {{base_url}}/organizations
X-CSRF-Token: {{csrf}}
{
    "full_name": "ООО Ромашка",
    "annual_turnover": 30,
    "employees_count": 3,
    "rating": 3,
    "type": "TRUST",
    "official_address": {
        "street": "Невский проспект",
        "town": null
    },
    "postal_address": {
        "street": "100%_lane",
        "town": null
    }
}
HTTP 201
[Captures]
first: jsonpath "$.id"

POST {{base_url}}/organizations
X-CSRF-Token: {{csrf}}
{
    "full_name": "Ромашка Плюс",
    "annual_turnover": 10,
    "employees_count": 3,
    "rating": 1,
    "type": "PUBLIC",
    "official_address": {
        "street": "abc",
        "town": null
    },
    "postal_address": {
        "street": "Kronverksky",
        "town": null
    }
}
HTTP 201
[Captures]
second: jsonpath "$.id"

POST {{base_url}}/organizations
X-CSRF-Token: {{csrf}}
{
    "full_name": "Zeta 100%_corp",
    "annual_turnover": 20,
    "employees_count": 3,
    "rating": 2,
    "type": null,
    "official_address": {
        "street": "Kronverksky",
        "town": null
    },
    "postal_address": {
        "street": "abc",
        "town": null
    }
}
HTTP 201
[Captures]
third: jsonpath "$.id"

GET {{base_url}}/organizations
[Query]
full_name: РОМАШ
HTTP 200
[Asserts]
jsonpath "$.total_items" == 2
jsonpath "$.items[0].id" == {{first}}
jsonpath "$.items[1].id" == {{second}}

GET {{base_url}}/organizations
[Query]
full_name: %_
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1
jsonpath "$.items[0].id" == {{third}}

GET {{base_url}}/organizations?type=tru
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1
jsonpath "$.items[0].id" == {{first}}

GET {{base_url}}/organizations?type=zzz
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0

GET {{base_url}}/organizations
[Query]
official_address_street: невск
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1
jsonpath "$.items[0].id" == {{first}}

GET {{base_url}}/organizations?postal_address_street=KRON
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1
jsonpath "$.items[0].id" == {{second}}

GET {{base_url}}/organizations
[Query]
full_name: ромашка
type: pub
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1
jsonpath "$.items[0].id" == {{second}}

GET {{base_url}}/organizations?type=
HTTP 200
[Asserts]
jsonpath "$.total_items" == 3

GET {{base_url}}/organizations?sort_by=type&sort_order=desc
HTTP 200
[Asserts]
jsonpath "$.items[0].id" == {{first}}
jsonpath "$.items[1].id" == {{second}}
jsonpath "$.items[2].id" == {{third}}

GET {{base_url}}/organizations?sort_by=type
HTTP 200
[Asserts]
jsonpath "$.items[0].id" == {{second}}
jsonpath "$.items[2].id" == {{third}}

GET {{base_url}}/organizations?sort_by=full_name
HTTP 200
[Asserts]
jsonpath "$.items[0].id" == {{third}}
jsonpath "$.items[1].id" == {{first}}
jsonpath "$.items[2].id" == {{second}}

GET {{base_url}}/organizations?sort_by=annual_turnover&sort_order=desc&page_size=2
HTTP 200
[Asserts]
jsonpath "$.total_items" == 3
jsonpath "$.items" count == 2
jsonpath "$.items[0].id" == {{first}}
jsonpath "$.items[1].id" == {{third}}

GET {{base_url}}/organizations?sort_by=official_address_street
HTTP 200
[Asserts]
jsonpath "$.items[0].id" == {{second}}
jsonpath "$.items[1].id" == {{third}}
jsonpath "$.items[2].id" == {{first}}

GET {{base_url}}/organizations?sort_by=postal_address_id&sort_order=desc
HTTP 200
[Asserts]
jsonpath "$.items[0].id" == {{third}}
jsonpath "$.items[2].id" == {{first}}

GET {{base_url}}/organizations?sort_by=address
HTTP 400
[Asserts]
jsonpath "$.errors[?(@.field == 'sort_by')].title" contains "INVALID_ENUM_VALUE"
