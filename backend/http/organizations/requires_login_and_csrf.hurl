GET {{base_url}}/organizations
HTTP 401

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "csrf_organization_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "csrf_organization_user",
    "password": "secret_1"
}
HTTP 200

POST {{base_url}}/organizations
{
    "full_name": "No Csrf",
    "annual_turnover": 1,
    "employees_count": 1,
    "rating": 1,
    "type": null,
    "official_address_id": 1,
    "postal_address_id": 1
}
HTTP 403

GET {{base_url}}/organizations
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0
