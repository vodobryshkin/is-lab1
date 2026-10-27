GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "worker_crud_user",
    "password": "secret_1"
}
HTTP 201
[Captures]
user_id: jsonpath "$.id"

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "worker_crud_user",
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
    "name": "Владимир Добрышкин",
    "coordinates": {
        "x": -953,
        "y": -391
    },
    "salary": 1500.5,
    "rating": 5,
    "start_date": "2026-10-01T09:00:00Z",
    "position": "LEAD_DEVELOPER",
    "status": "REGULAR",
    "person": {
        "eye_color": "BLUE",
        "hair_color": "BROWN",
        "location": {
            "x": 1,
            "y": 2,
            "z": 3
        },
        "birthday": "2000-05-01T00:00:00Z",
        "weight": 70.5,
        "nationality": "RUSSIA"
    },
    "organization": {
        "full_name": "ООО ЛТК Магистраль",
        "annual_turnover": 1000000.5,
        "employees_count": 3,
        "rating": 1000,
        "type": "PUBLIC",
        "official_address": {
            "street": "Ул. Пушкина",
            "town": {
                "x": 4,
                "y": 5,
                "z": 6
            }
        },
        "postal_address": {
            "street": "Kronverksky",
            "town": null
        }
    }
}
HTTP 201
[Captures]
id: jsonpath "$.id"
organization_id: jsonpath "$.organization_id"
[Asserts]
header "Location" matches "/api/v1/workers/\\d+$"
jsonpath "$.id" > 0
jsonpath "$.organization_id" > 0

GET {{base_url}}/workers/{{id}}
HTTP 200
[Captures]
person_id: jsonpath "$.person.id"
coordinates_id: jsonpath "$.coordinates.id"
creation_date: jsonpath "$.creation_date"
[Asserts]
jsonpath "$.id" == {{id}}
jsonpath "$.name" == "Владимир Добрышкин"
jsonpath "$.coordinates.x" == -953
jsonpath "$.coordinates.y" == -391
jsonpath "$.salary" == 1500.5
jsonpath "$.rating" == 5
jsonpath "$.start_date" == "2026-10-01T09:00:00Z"
jsonpath "$.creation_date" isIsoDate
jsonpath "$.position" == "LEAD_DEVELOPER"
jsonpath "$.status" == "REGULAR"
jsonpath "$.creator_id" == {{user_id}}
jsonpath "$.organization.id" == {{organization_id}}
jsonpath "$.organization.full_name" == "ООО ЛТК Магистраль"
jsonpath "$.organization.official_address.town.z" == 6
jsonpath "$.organization.postal_address.town" == null
jsonpath "$.person.eye_color" == "BLUE"
jsonpath "$.person.location.z" == 3
jsonpath "$.person.birthday" == "2000-05-01T00:00:00Z"
jsonpath "$.person.worker" not exists
jsonpath "$.person.worker_id" not exists
jsonpath "$.organization_id" not exists
jsonpath "$.user" not exists

PUT {{base_url}}/workers/{{id}}
X-CSRF-Token: {{csrf}}
{
    "name": "Renamed Worker",
    "coordinates": {
        "x": 10,
        "y": 20
    },
    "salary": 2000,
    "rating": 7,
    "start_date": "2026-10-02T09:00:00Z",
    "position": null,
    "status": null,
    "person": null,
    "organization_id": null
}
HTTP 204

GET {{base_url}}/workers/{{id}}
HTTP 200
[Asserts]
jsonpath "$.name" == "Renamed Worker"
jsonpath "$.coordinates.x" == 10
jsonpath "$.coordinates.id" != {{coordinates_id}}
jsonpath "$.salary" == 2000
jsonpath "$.rating" == 7
jsonpath "$.start_date" == "2026-10-02T09:00:00Z"
jsonpath "$.creation_date" == "{{creation_date}}"
jsonpath "$.creator_id" == {{user_id}}
jsonpath "$.position" == null
jsonpath "$.status" == null
jsonpath "$.person" == null
jsonpath "$.organization" == null

GET {{base_url}}/persons/{{person_id}}
HTTP 200

GET {{base_url}}/organizations/{{organization_id}}
HTTP 200

GET {{base_url}}/coordinates/{{coordinates_id}}
HTTP 200

DELETE {{base_url}}/workers/{{id}}
X-CSRF-Token: {{csrf}}
HTTP 204

GET {{base_url}}/workers/{{id}}
HTTP 404
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.message" == "Worker with id={{id}} does not exist."

DELETE {{base_url}}/workers/{{id}}
X-CSRF-Token: {{csrf}}
HTTP 404
[Asserts]
jsonpath "$.message" == "Worker with id={{id}} does not exist."

PUT {{base_url}}/workers/{{id}}
X-CSRF-Token: {{csrf}}
{
    "name": "Missing",
    "coordinates": {
        "id": {{coordinates_id}}
    },
    "salary": 1,
    "rating": 1,
    "start_date": "2026-10-02T09:00:00Z",
    "position": null,
    "status": null,
    "person": null,
    "organization_id": null
}
HTTP 404
[Asserts]
jsonpath "$.message" == "Worker with id={{id}} does not exist."
