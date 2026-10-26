GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "worker_references_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "worker_references_user",
    "password": "secret_1"
}
HTTP 200

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/workers
X-CSRF-Token: {{csrf}}
{
    "name": "Refused",
    "coordinates": {"id": 999},
    "salary": 100,
    "rating": 1,
    "start_date": "2026-10-01T09:00:00Z",
    "position": null,
    "status": null,
    "person": {"eye_color": null, "hair_color": "BLUE", "location": {"x": 1, "y": 1, "z": 1}, "birthday": "2000-01-01T00:00:00Z", "weight": 50, "nationality": "SPAIN"},
    "organization": {"full_name": "Refused Co", "annual_turnover": 10, "employees_count": 1, "rating": 1, "type": null, "official_address": {"street": "Official", "town": {"x": 3, "y": 3, "z": 3}}, "postal_address": {"street": "Postal", "town": {"x": 2, "y": 2, "z": 2}}}
}
HTTP 404
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/coordinates-not-found"
jsonpath "$.status" == 404
jsonpath "$.coordinates_id" == 999

POST {{base_url}}/workers
X-CSRF-Token: {{csrf}}
{
    "name": "Refused",
    "coordinates": {"x": 1, "y": 1},
    "salary": 100,
    "rating": 1,
    "start_date": "2026-10-01T09:00:00Z",
    "position": null,
    "status": null,
    "person": {"eye_color": null, "hair_color": "BLUE", "location": {"x": 1, "y": 1, "z": 1}, "birthday": "2000-01-01T00:00:00Z", "weight": 50, "nationality": "SPAIN"},
    "organization_id": 999
}
HTTP 404
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/organization-not-found"
jsonpath "$.status" == 404
jsonpath "$.organization_id" == 999

POST {{base_url}}/workers
X-CSRF-Token: {{csrf}}
{
    "name": "Refused",
    "coordinates": {"x": 1, "y": 1},
    "salary": 100,
    "rating": 1,
    "start_date": "2026-10-01T09:00:00Z",
    "position": null,
    "status": null,
    "person": {"id": 999},
    "organization": {"full_name": "Refused Co", "annual_turnover": 10, "employees_count": 1, "rating": 1, "type": null, "official_address": {"street": "Official", "town": {"x": 3, "y": 3, "z": 3}}, "postal_address": {"street": "Postal", "town": {"x": 2, "y": 2, "z": 2}}}
}
HTTP 404
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/person-not-found"
jsonpath "$.status" == 404
jsonpath "$.person_id" == 999

POST {{base_url}}/workers
X-CSRF-Token: {{csrf}}
{
    "name": "Refused",
    "coordinates": {"x": 1, "y": 1},
    "salary": 100,
    "rating": 1,
    "start_date": "2026-10-01T09:00:00Z",
    "position": null,
    "status": null,
    "person": {"eye_color": null, "hair_color": "BLUE", "location": {"id": 999}, "birthday": "2000-01-01T00:00:00Z", "weight": 50, "nationality": "SPAIN"},
    "organization": {"full_name": "Refused Co", "annual_turnover": 10, "employees_count": 1, "rating": 1, "type": null, "official_address": {"street": "Official", "town": {"x": 3, "y": 3, "z": 3}}, "postal_address": {"street": "Postal", "town": {"x": 2, "y": 2, "z": 2}}}
}
HTTP 404
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/location-not-found"
jsonpath "$.status" == 404
jsonpath "$.location_id" == 999

POST {{base_url}}/workers
X-CSRF-Token: {{csrf}}
{
    "name": "Refused",
    "coordinates": {"x": 1, "y": 1},
    "salary": 100,
    "rating": 1,
    "start_date": "2026-10-01T09:00:00Z",
    "position": null,
    "status": null,
    "person": {"eye_color": null, "hair_color": "BLUE", "location": {"x": 1, "y": 1, "z": 1}, "birthday": "2000-01-01T00:00:00Z", "weight": 50, "nationality": "SPAIN"},
    "organization": {"full_name": "Refused Co", "annual_turnover": 10, "employees_count": 1, "rating": 1, "type": null, "official_address": {"street": "Official", "town": {"x": 3, "y": 3, "z": 3}}, "postal_address_id": 999}
}
HTTP 404
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/address-not-found"
jsonpath "$.status" == 404

GET {{base_url}}/workers
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0

GET {{base_url}}/coordinates
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0

GET {{base_url}}/persons
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0

GET {{base_url}}/locations
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0

GET {{base_url}}/addresses
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0

GET {{base_url}}/organizations
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0

POST {{base_url}}/organizations
X-CSRF-Token: {{csrf}}
{
    "full_name": "Taken Name",
    "annual_turnover": 10,
    "employees_count": 1,
    "rating": 1,
    "type": null,
    "official_address": {
        "street": "Taken",
        "town": null
    },
    "postal_address": {
        "street": "Taken",
        "town": null
    }
}
HTTP 201

POST {{base_url}}/workers
X-CSRF-Token: {{csrf}}
{
    "name": "Refused",
    "coordinates": {"x": 1, "y": 1},
    "salary": 100,
    "rating": 1,
    "start_date": "2026-10-01T09:00:00Z",
    "position": null,
    "status": null,
    "person": {"eye_color": null, "hair_color": "BLUE", "location": {"x": 1, "y": 1, "z": 1}, "birthday": "2000-01-01T00:00:00Z", "weight": 50, "nationality": "SPAIN"},
    "organization": {"full_name": "Taken Name", "annual_turnover": 10, "employees_count": 1, "rating": 1, "type": null, "official_address": {"street": "Official", "town": {"x": 3, "y": 3, "z": 3}}, "postal_address": {"street": "Postal", "town": {"x": 2, "y": 2, "z": 2}}}
}
HTTP 409
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/organization-name-already-exists"
jsonpath "$.status" == 409
jsonpath "$.full_name" == "Taken Name"

GET {{base_url}}/workers
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0

GET {{base_url}}/organizations
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1

GET {{base_url}}/persons
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0

POST {{base_url}}/workers
X-CSRF-Token: {{csrf}}
{
    "name": "Refused",
    "coordinates": {"x": 1, "y": 1},
    "salary": 100,
    "rating": 1,
    "start_date": "2026-10-01T09:00:00Z",
    "position": null,
    "status": null,
    "person": null
}
HTTP 201
[Captures]
id: jsonpath "$.id"

PUT {{base_url}}/workers/{{id}}
X-CSRF-Token: {{csrf}}
{
    "name": "Refused",
    "coordinates": {"x": 5, "y": 5},
    "salary": 100,
    "rating": 1,
    "start_date": "2026-10-01T09:00:00Z",
    "position": null,
    "status": null,
    "person": null,
    "organization_id": 999
}
HTTP 404
[Asserts]
jsonpath "$.type" == "/problems/organization-not-found"
jsonpath "$.organization_id" == 999

PUT {{base_url}}/workers/{{id}}
X-CSRF-Token: {{csrf}}
{
    "name": "Refused",
    "coordinates": {"x": 5, "y": 5},
    "salary": 100,
    "rating": 1,
    "start_date": "2026-10-01T09:00:00Z",
    "position": null,
    "status": null,
    "person": {"id": 999},
    "organization_id": null
}
HTTP 404
[Asserts]
jsonpath "$.type" == "/problems/person-not-found"
jsonpath "$.person_id" == 999

GET {{base_url}}/workers/{{id}}
HTTP 200
[Asserts]
jsonpath "$.coordinates.x" == 1

GET {{base_url}}/coordinates
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1
