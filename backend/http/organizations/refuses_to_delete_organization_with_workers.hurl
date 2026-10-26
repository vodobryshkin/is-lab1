GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "delete_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "delete_user",
    "password": "secret_1"
}
HTTP 200

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

DELETE {{base_url}}/organizations/1
X-CSRF-Token: {{csrf}}
HTTP 409
[Asserts]
jsonpath "$.status" == 409
jsonpath "$.type" == "/problems/organization-has-workers"
jsonpath "$.organization_id" == 1

GET {{base_url}}/organizations/1
HTTP 200
[Asserts]
jsonpath "$.full_name" == "Employer"

PUT {{base_url}}/organizations/1
X-CSRF-Token: {{csrf}}
{
    "full_name": "Employer Renamed",
    "annual_turnover": 1,
    "employees_count": 1,
    "rating": 1,
    "type": null,
    "official_address_id": 1,
    "postal_address_id": 2
}
HTTP 204

DELETE {{base_url}}/organizations/1
X-CSRF-Token: {{csrf}}
HTTP 409

GET {{base_url}}/addresses
HTTP 200
[Asserts]
jsonpath "$.total_items" == 2
