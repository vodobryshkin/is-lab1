GET {{base_url}}/persons
HTTP 401

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "csrf_person_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "csrf_person_user",
    "password": "secret_1"
}
HTTP 200

POST {{base_url}}/persons
{
    "eye_color": null,
    "hair_color": "BLACK",
    "location": {
        "x": 1,
        "y": 1,
        "z": 1
    },
    "birthday": "2000-01-01T00:00:00Z",
    "weight": 70,
    "nationality": "RUSSIA"
}
HTTP 403

GET {{base_url}}/persons
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0
