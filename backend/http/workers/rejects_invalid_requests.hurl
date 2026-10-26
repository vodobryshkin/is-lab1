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
jsonpath "$.type" == "/problems/invalid-worker-id"
jsonpath "$.status" == 400
jsonpath "$.errors[0].field" == "id"
jsonpath "$.errors[0].title" == "INVALID_TYPE"

GET {{base_url}}/workers/0
HTTP 400
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/invalid-worker-id"
jsonpath "$.status" == 400
jsonpath "$.errors[0].field" == "id"
jsonpath "$.errors[0].title" == "MUST_BE_POSITIVE"

GET {{base_url}}/workers/2147483648
HTTP 400
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/invalid-worker-id"
jsonpath "$.status" == 400
jsonpath "$.errors[0].field" == "id"
jsonpath "$.errors[0].title" == "OUT_OF_RANGE"

DELETE {{base_url}}/workers/0
X-CSRF-Token: {{csrf}}
HTTP 400
[Asserts]
jsonpath "$.type" == "/problems/invalid-worker-id"

POST {{base_url}}/workers
X-CSRF-Token: {{csrf}}
Content-Type: application/json
```
{"name": 
```
HTTP 400
[Asserts]
jsonpath "$.type" == "/problems/malformed-json"
jsonpath "$.status" == 400

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
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.status" == 422
jsonpath "$.errors[?(@.field == 'name')].title" exists
jsonpath "$.errors[?(@.field == 'coordinates.x')].title" == "MUST_BE_GREATER_THAN_MINUS_954"
jsonpath "$.errors[?(@.field == 'salary')].title" == "MUST_BE_POSITIVE"
jsonpath "$.errors[?(@.field == 'rating')].title" == "MUST_BE_POSITIVE"
jsonpath "$.errors[?(@.field == 'start_date')].title" == "INVALID_DATE_TIME"
jsonpath "$.errors[?(@.field == 'position')].title" == "INVALID_ENUM_VALUE"
jsonpath "$.errors[?(@.field == 'organization_id')].title" == "MUTUALLY_EXCLUSIVE"
jsonpath "$.errors[?(@.field == 'creator_id')].title" == "UNKNOWN_FIELD"

POST {{base_url}}/workers
X-CSRF-Token: {{csrf}}
{
    "name": "No Person Key"
}
HTTP 422
[Asserts]
jsonpath "$.errors[?(@.field == 'person')].title" == "REQUIRED"
jsonpath "$.errors[?(@.field == 'coordinates')].title" == "REQUIRED"

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
jsonpath "$.errors[?(@.field == 'organization_id')].title" == "REQUIRED"

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
jsonpath "$.errors[?(@.field == 'organization')].title" == "UNKNOWN_FIELD"

GET {{base_url}}/workers
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0
