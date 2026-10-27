GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "name_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "name_user",
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
    "full_name": "Магистраль",
    "annual_turnover": 1,
    "employees_count": 3,
    "rating": 1,
    "type": null,
    "official_address": {
        "street": "A",
        "town": null
    },
    "postal_address": {
        "street": "B",
        "town": null
    }
}
HTTP 201
[Captures]
first: jsonpath "$.id"

POST {{base_url}}/organizations
X-CSRF-Token: {{csrf}}
{
    "full_name": "Other",
    "annual_turnover": 1,
    "employees_count": 3,
    "rating": 1,
    "type": null,
    "official_address": {
        "street": "C",
        "town": null
    },
    "postal_address": {
        "street": "D",
        "town": null
    }
}
HTTP 201
[Captures]
second: jsonpath "$.id"

POST {{base_url}}/organizations
X-CSRF-Token: {{csrf}}
{
    "full_name": "Магистраль",
    "annual_turnover": 1,
    "employees_count": 1,
    "rating": 1,
    "type": null,
    "official_address": {
        "street": "Would Be Created",
        "town": null
    },
    "postal_address": {
        "street": "Would Be Created",
        "town": null
    }
}
HTTP 409
[Asserts]
jsonpath "$.message" == "Another organization with this full_name already exists."

GET {{base_url}}/addresses
HTTP 200
[Asserts]
jsonpath "$.total_items" == 4

PUT {{base_url}}/organizations/{{second}}
X-CSRF-Token: {{csrf}}
{
    "full_name": "Магистраль",
    "annual_turnover": 1,
    "employees_count": 1,
    "rating": 1,
    "type": null,
    "official_address_id": 1,
    "postal_address_id": 2
}
HTTP 409
[Asserts]
jsonpath "$.message" == "Another organization with this full_name already exists."

GET {{base_url}}/organizations/{{second}}
HTTP 200
[Asserts]
jsonpath "$.full_name" == "Other"

PUT {{base_url}}/organizations/{{first}}
X-CSRF-Token: {{csrf}}
{
    "full_name": "Магистраль",
    "annual_turnover": 9,
    "employees_count": 1,
    "rating": 1,
    "type": null,
    "official_address_id": 1,
    "postal_address_id": 2
}
HTTP 204

GET {{base_url}}/organizations/{{first}}
HTTP 200
[Asserts]
jsonpath "$.annual_turnover" == 9
