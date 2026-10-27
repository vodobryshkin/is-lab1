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
jsonpath "$.message" == "Coordinates with id=999 do not exist."

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
jsonpath "$.message" == "Organization with id=999 does not exist."

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
jsonpath "$.message" == "Person with id=999 does not exist."

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
jsonpath "$.message" == "Location with id=999 does not exist."

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
jsonpath "$.message" == "Address with id=999 does not exist."

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
jsonpath "$.message" == "Another organization with this full_name already exists."

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
jsonpath "$.message" == "Organization with id=999 does not exist."

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
jsonpath "$.message" == "Person with id=999 does not exist."

GET {{base_url}}/workers/{{id}}
HTTP 200
[Asserts]
jsonpath "$.coordinates.x" == 1

GET {{base_url}}/coordinates
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1
