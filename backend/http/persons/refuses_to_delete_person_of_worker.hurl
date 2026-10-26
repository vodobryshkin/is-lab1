GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "delete_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "delete_user",
    "password": "secret_1"
}
HTTP 200

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

DELETE {{base_url}}/persons/1
X-CSRF-Token: {{csrf}}
HTTP 409
[Asserts]
jsonpath "$.status" == 409
jsonpath "$.type" == "/problems/person-is-in-use"
jsonpath "$.person_id" == 1

PUT {{base_url}}/persons/1
X-CSRF-Token: {{csrf}}
{
    "eye_color": "BLUE",
    "hair_color": "BROWN",
    "location": {
        "id": 1
    },
    "birthday": "2000-05-06T07:08:09Z",
    "weight": 71,
    "nationality": "RUSSIA"
}
HTTP 204

DELETE {{base_url}}/persons/1
X-CSRF-Token: {{csrf}}
HTTP 409

GET {{base_url}}/persons/1
HTTP 200
[Asserts]
jsonpath "$.eye_color" == "BLUE"

DELETE {{base_url}}/persons/2
X-CSRF-Token: {{csrf}}
HTTP 204

GET {{base_url}}/locations/1
HTTP 200
