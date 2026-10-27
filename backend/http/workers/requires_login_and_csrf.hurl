GET {{base_url}}/workers
HTTP 401

GET {{base_url}}/workers/1
HTTP 401

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "worker_csrf_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "worker_csrf_user",
    "password": "secret_1"
}
HTTP 200

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/workers
{
    "name": "No Csrf",
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
HTTP 403
[Asserts]
jsonpath "$.message" == "The X-CSRF-Token header is missing or does not match the current session."

POST {{base_url}}/workers
X-CSRF-Token: wrong-token
{
    "name": "Wrong Csrf",
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
HTTP 403

GET {{base_url}}/workers
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0

GET {{base_url}}/coordinates
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0
