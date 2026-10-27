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
jsonpath "$.message" == "The request body contains invalid JSON."

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
    "start_date": "not a date",
    "position": "CLEANER",
    "status": null,
    "person": null,
    "organization_id": 1,
    "organization": null,
    "creator_id": 1
}
HTTP 422
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.errors[?(@.field == 'name')].code" exists
jsonpath "$.errors[?(@.field == 'coordinates.x')].code" == "MUST_BE_GREATER_THAN_MINUS_954"
jsonpath "$.errors[?(@.field == 'salary')].code" == "MUST_BE_POSITIVE"
jsonpath "$.errors[?(@.field == 'rating')].code" == "MUST_BE_POSITIVE"
jsonpath "$.errors[?(@.field == 'start_date')].code" == "INVALID_DATE_TIME"
jsonpath "$.errors[?(@.field == 'position')].code" == "INVALID_ENUM_VALUE"
jsonpath "$.errors[?(@.field == 'organization_id')].code" == "MUTUALLY_EXCLUSIVE"
jsonpath "$.errors[?(@.field == 'creator_id')].code" == "UNKNOWN_FIELD"

POST {{base_url}}/workers
X-CSRF-Token: {{csrf}}
{
    "name": "No Person Key"
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'person')].code" == "REQUIRED"
jsonpath "$.errors[?(@.field == 'coordinates')].code" == "REQUIRED"

PUT {{base_url}}/workers/1
X-CSRF-Token: {{csrf}}
{
    "name": "Update Without Organization Key",
    "coordinates": {
        "x": 1,
        "y": 1
    },
    "salary": 1,
    "rating": 1,
    "start_date": "2026-10-01T09:00:00Z",
    "position": null,
    "status": null,
    "person": null
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'organization_id')].code" == "REQUIRED"

PUT {{base_url}}/workers/1
X-CSRF-Token: {{csrf}}
{
    "name": "Update With New Organization",
    "coordinates": {
        "x": 1,
        "y": 1
    },
    "salary": 1,
    "rating": 1,
    "start_date": "2026-10-01T09:00:00Z",
    "position": null,
    "status": null,
    "person": null,
    "organization_id": null,
    "organization": null
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'organization')].code" == "UNKNOWN_FIELD"

GET {{base_url}}/workers
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0
