GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "worker_invalid_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "worker_invalid_user",
    "password": "secret_1"
}
HTTP 200

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

GET {{base_url}}/workers/abc
HTTP 400
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.message" == "Parameter id must be an integer."

GET {{base_url}}/workers/0
HTTP 400
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.message" == "Parameter id must be greater than zero."

GET {{base_url}}/workers/2147483648
HTTP 400
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.message" == "Parameter id must not exceed 2147483647."

DELETE {{base_url}}/workers/0
X-CSRF-Token: {{csrf}}
HTTP 400
[Asserts]
jsonpath "$.message" == "Parameter id must be greater than zero."

POST {{base_url}}/workers
X-CSRF-Token: {{csrf}}
Content-Type: application/json
```
{"name": 
```
HTTP 400
[Asserts]
jsonpath "$.message" == "The request body contains invalid JSON or a value of the wrong type."

POST {{base_url}}/workers
X-CSRF-Token: {{csrf}}
{
    "name": "",
    "coordinates": {
        "x": -954,
        "y": -391
    },
    "salary": 0,
    "rating": 0,
    "status": null,
    "person": null,
    "organization_id": 1,
    "organization": {
        "full_name": "Both",
        "annual_turnover": 1,
        "employees_count": 1,
        "rating": 1,
        "official_address_id": 1,
        "postal_address_id": 1
    },
    "creator_id": 1
}
HTTP 422
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.errors[?(@.field == 'name')].code" exists
jsonpath "$.errors[?(@.field == 'coordinates.x')].code" == "MUST_BE_GREATER_THAN_MINUS_954"
jsonpath "$.errors[?(@.field == 'salary')].code" == "MUST_BE_POSITIVE"
jsonpath "$.errors[?(@.field == 'rating')].code" == "MUST_BE_POSITIVE"
jsonpath "$.errors[?(@.field == 'start_date')].code" == "MUST_NOT_BE_NULL"
jsonpath "$.errors[?(@.field == 'organization')].code" == "INVALID_COMBINATION"

POST {{base_url}}/workers
X-CSRF-Token: {{csrf}}
{
    "name": "Wrong Date",
    "coordinates": {
        "x": 1,
        "y": 1
    },
    "salary": 1,
    "rating": 1,
    "start_date": "not a date",
    "position": "CLEANER"
}
HTTP 400
[Asserts]
jsonpath "$.message" == "The request body contains invalid JSON or a value of the wrong type."

POST {{base_url}}/workers
X-CSRF-Token: {{csrf}}
{
    "name": "No Person Key"
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'person')]" not exists
jsonpath "$.errors[?(@.field == 'coordinates')].code" == "MUST_NOT_BE_NULL"
jsonpath "$.errors[?(@.field == 'salary')].code" == "MUST_NOT_BE_NULL"

GET {{base_url}}/workers
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0
