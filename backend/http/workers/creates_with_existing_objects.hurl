GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "worker_existing_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "worker_existing_user",
    "password": "secret_1"
}
HTTP 200

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/coordinates
X-CSRF-Token: {{csrf}}
{
    "x": 1,
    "y": 2
}
HTTP 201
[Captures]
coordinates_id: jsonpath "$.id"

POST {{base_url}}/locations
X-CSRF-Token: {{csrf}}
{
    "x": 7,
    "y": 8,
    "z": 9
}
HTTP 201
[Captures]
location_id: jsonpath "$.id"

POST {{base_url}}/persons
X-CSRF-Token: {{csrf}}
{
    "eye_color": null,
    "hair_color": "BLACK",
    "location": {
        "id": {{location_id}}
    },
    "birthday": "1999-01-01T00:00:00Z",
    "weight": 60,
    "nationality": "SPAIN"
}
HTTP 201
[Captures]
person_id: jsonpath "$.id"

POST {{base_url}}/organizations
X-CSRF-Token: {{csrf}}
{
    "full_name": "Existing Employer",
    "annual_turnover": 10,
    "employees_count": 1,
    "rating": 1,
    "type": null,
    "official_address": {
        "street": "Official",
        "town": null
    },
    "postal_address": {
        "street": "Postal",
        "town": null
    }
}
HTTP 201
[Captures]
organization_id: jsonpath "$.id"

POST {{base_url}}/workers
X-CSRF-Token: {{csrf}}
{
    "name": "Existing Objects",
    "coordinates": {
        "id": {{coordinates_id}}
    },
    "salary": 100,
    "rating": 1,
    "start_date": "2026-10-01T09:00:00Z",
    "position": null,
    "status": "HIRED",
    "person": {
        "id": {{person_id}}
    },
    "organization_id": {{organization_id}}
}
HTTP 201
[Captures]
id: jsonpath "$.id"
[Asserts]
jsonpath "$.organization_id" == {{organization_id}}

GET {{base_url}}/workers/{{id}}
HTTP 200
[Asserts]
jsonpath "$.coordinates.id" == {{coordinates_id}}
jsonpath "$.person.id" == {{person_id}}
jsonpath "$.person.location.id" == {{location_id}}
jsonpath "$.organization.id" == {{organization_id}}

POST {{base_url}}/workers
X-CSRF-Token: {{csrf}}
{
    "name": "Without Organization",
    "coordinates": {
        "id": {{coordinates_id}}
    },
    "salary": 100,
    "rating": 1,
    "start_date": "2026-10-01T09:00:00Z",
    "position": null,
    "status": null,
    "person": {
        "id": {{person_id}}
    }
}
HTTP 409
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.message" == "Person with id={{person_id}} is already assigned to another worker."

POST {{base_url}}/workers
X-CSRF-Token: {{csrf}}
{
    "name": "Without Organization",
    "coordinates": {
        "id": {{coordinates_id}}
    },
    "salary": 100,
    "rating": 1,
    "start_date": "2026-10-01T09:00:00Z",
    "position": null,
    "status": null,
    "person": {
        "eye_color": null,
        "hair_color": "WHITE",
        "location": {
            "id": {{location_id}}
        },
        "birthday": "1990-01-01T00:00:00Z",
        "weight": 80,
        "nationality": "ITALY"
    }
}
HTTP 201
[Captures]
second_id: jsonpath "$.id"
[Asserts]
jsonpath "$.organization_id" == null

GET {{base_url}}/workers/{{second_id}}
HTTP 200
[Captures]
second_person_id: jsonpath "$.person.id"
[Asserts]
jsonpath "$.organization" == null
jsonpath "$.person.location.id" == {{location_id}}

PUT {{base_url}}/workers/{{id}}
X-CSRF-Token: {{csrf}}
{
    "name": "Existing Objects",
    "coordinates": {
        "id": {{coordinates_id}}
    },
    "salary": 100,
    "rating": 2,
    "start_date": "2026-10-01T09:00:00Z",
    "position": null,
    "status": "HIRED",
    "person": {
        "id": {{person_id}}
    },
    "organization_id": {{organization_id}}
}
HTTP 204

PUT {{base_url}}/workers/{{id}}
X-CSRF-Token: {{csrf}}
{
    "name": "Existing Objects",
    "coordinates": {
        "id": {{coordinates_id}}
    },
    "salary": 100,
    "rating": 2,
    "start_date": "2026-10-01T09:00:00Z",
    "position": null,
    "status": "HIRED",
    "person": {
        "id": {{second_person_id}}
    },
    "organization_id": {{organization_id}}
}
HTTP 409
[Asserts]
jsonpath "$.message" == "Person with id={{second_person_id}} is already assigned to another worker."

DELETE {{base_url}}/workers/{{id}}
X-CSRF-Token: {{csrf}}
HTTP 409
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.message" == "Worker with id={{id}} is referenced by a person and cannot be deleted."

PUT {{base_url}}/workers/{{second_id}}
X-CSRF-Token: {{csrf}}
{
    "name": "Without Organization",
    "coordinates": {
        "id": {{coordinates_id}}
    },
    "salary": 100,
    "rating": 1,
    "start_date": "2026-10-01T09:00:00Z",
    "position": null,
    "status": null,
    "person": {
        "id": {{person_id}}
    },
    "organization_id": null
}
HTTP 409

PUT {{base_url}}/workers/{{id}}
X-CSRF-Token: {{csrf}}
{
    "name": "Existing Objects",
    "coordinates": {
        "id": {{coordinates_id}}
    },
    "salary": 100,
    "rating": 2,
    "start_date": "2026-10-01T09:00:00Z",
    "position": null,
    "status": "HIRED",
    "person": null,
    "organization_id": {{organization_id}}
}
HTTP 204

PUT {{base_url}}/workers/{{second_id}}
X-CSRF-Token: {{csrf}}
{
    "name": "Without Organization",
    "coordinates": {
        "id": {{coordinates_id}}
    },
    "salary": 100,
    "rating": 1,
    "start_date": "2026-10-01T09:00:00Z",
    "position": null,
    "status": null,
    "person": {
        "id": {{person_id}}
    },
    "organization_id": null
}
HTTP 204

GET {{base_url}}/workers/{{second_id}}
HTTP 200
[Asserts]
jsonpath "$.person.id" == {{person_id}}

GET {{base_url}}/persons/{{second_person_id}}
HTTP 200

DELETE {{base_url}}/workers/{{id}}
X-CSRF-Token: {{csrf}}
HTTP 204

GET {{base_url}}/coordinates/{{coordinates_id}}
HTTP 200

GET {{base_url}}/organizations/{{organization_id}}
HTTP 200
