GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "crud_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "crud_user",
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
    "full_name": "ООО ЛТК Магистраль",
    "annual_turnover": 1000000.5,
    "employees_count": 3,
    "rating": 1000,
    "type": "OPEN_JOINT_STOCK_COMPANY",
    "official_address": {
        "street": "Ул. Пушкина",
        "town": {
            "x": 1,
            "y": 2,
            "z": 3
        }
    },
    "postal_address": {
        "street": "Kronverksky",
        "town": null
    }
}
HTTP 201
[Captures]
id: jsonpath "$.id"
[Asserts]
header "Location" matches "/api/v1/organizations/\\d+$"
jsonpath "$.id" > 0

GET {{base_url}}/organizations/{{id}}
HTTP 200
[Captures]
official_id: jsonpath "$.official_address.id"
postal_id: jsonpath "$.postal_address.id"
[Asserts]
jsonpath "$.id" == {{id}}
jsonpath "$.full_name" == "ООО ЛТК Магистраль"
jsonpath "$.annual_turnover" == 1000000.5
jsonpath "$.employees_count" == 3
jsonpath "$.rating" == 1000
jsonpath "$.type" == "OPEN_JOINT_STOCK_COMPANY"
jsonpath "$.official_address.street" == "Ул. Пушкина"
jsonpath "$.official_address.town.z" == 3
jsonpath "$.postal_address.street" == "Kronverksky"
jsonpath "$.postal_address.town" == null
jsonpath "$.official_address_id" not exists
jsonpath "$.postal_address_id" not exists

POST {{base_url}}/organizations
X-CSRF-Token: {{csrf}}
{
    "full_name": "Existing Addresses",
    "annual_turnover": 1,
    "employees_count": 1,
    "rating": 1,
    "type": null,
    "official_address_id": {{official_id}},
    "postal_address_id": {{postal_id}}
}
HTTP 201
[Captures]
second_id: jsonpath "$.id"

GET {{base_url}}/organizations/{{second_id}}
HTTP 200
[Asserts]
jsonpath "$.type" == null
jsonpath "$.official_address.id" == {{official_id}}
jsonpath "$.postal_address.id" == {{postal_id}}

POST {{base_url}}/organizations
X-CSRF-Token: {{csrf}}
{
    "full_name": "New Official",
    "annual_turnover": 1,
    "employees_count": 1,
    "rating": 1,
    "type": "TRUST",
    "official_address": {
        "street": "Official Street",
        "town": {
            "id": 1
        }
    },
    "postal_address_id": {{postal_id}}
}
HTTP 201
[Captures]
third_id: jsonpath "$.id"

GET {{base_url}}/organizations/{{third_id}}
HTTP 200
[Asserts]
jsonpath "$.official_address.street" == "Official Street"
jsonpath "$.official_address.town.id" == 1
jsonpath "$.postal_address.id" == {{postal_id}}

POST {{base_url}}/organizations
X-CSRF-Token: {{csrf}}
{
    "full_name": "New Postal",
    "annual_turnover": 1,
    "employees_count": 1,
    "rating": 1,
    "type": "TRUST",
    "official_address_id": {{official_id}},
    "postal_address": {
        "street": "Postal Street",
        "town": null
    }
}
HTTP 201

PUT {{base_url}}/organizations/{{id}}
X-CSRF-Token: {{csrf}}
{
    "full_name": "ООО ЛТК Магистраль",
    "annual_turnover": 5,
    "employees_count": 7,
    "rating": 2,
    "type": null,
    "official_address_id": {{postal_id}},
    "postal_address": {
        "street": "Replaced Postal",
        "town": null
    }
}
HTTP 204

GET {{base_url}}/organizations/{{id}}
HTTP 200
[Asserts]
jsonpath "$.annual_turnover" == 5
jsonpath "$.employees_count" == 7
jsonpath "$.type" == null
jsonpath "$.official_address.id" == {{postal_id}}
jsonpath "$.postal_address.street" == "Replaced Postal"

GET {{base_url}}/addresses/{{official_id}}
HTTP 200

DELETE {{base_url}}/organizations/{{second_id}}
X-CSRF-Token: {{csrf}}
HTTP 204

GET {{base_url}}/organizations/{{second_id}}
HTTP 404
[Asserts]
jsonpath "$.message" == "Organization with id={{second_id}} does not exist."

GET {{base_url}}/addresses/{{official_id}}
HTTP 200

DELETE {{base_url}}/organizations/{{second_id}}
X-CSRF-Token: {{csrf}}
HTTP 404

PUT {{base_url}}/organizations/{{second_id}}
X-CSRF-Token: {{csrf}}
{
    "full_name": "Gone",
    "annual_turnover": 1,
    "employees_count": 1,
    "rating": 1,
    "type": null,
    "official_address_id": {{official_id}},
    "postal_address_id": {{postal_id}}
}
HTTP 404
[Asserts]
jsonpath "$.message" == "Organization with id={{second_id}} does not exist."
