GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "crud_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "crud_user",
    "password": "secret_1"
}
HTTP 200

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/persons
X-CSRF-Token: {{csrf}}
{
    "eye_color": null,
    "hair_color": "BLACK",
    "location": {
        "x": 1,
        "y": 2,
        "z": 3
    },
    "birthday": "2000-05-06T10:08:09+03:00",
    "weight": 70.5,
    "nationality": "RUSSIA"
}
HTTP 201
[Captures]
id: jsonpath "$.id"

GET {{base_url}}/persons/{{id}}
HTTP 200
[Captures]
location_id: jsonpath "$.location.id"
[Asserts]
jsonpath "$.id" == {{id}}
jsonpath "$.eye_color" == null
jsonpath "$.hair_color" == "BLACK"
jsonpath "$.location.x" == 1
jsonpath "$.location.z" == 3
jsonpath "$.birthday" == "2000-05-06T07:08:09Z"
jsonpath "$.weight" == 70.5
jsonpath "$.nationality" == "RUSSIA"
jsonpath "$.worker" not exists

POST {{base_url}}/persons
X-CSRF-Token: {{csrf}}
{
    "eye_color": "BLUE",
    "hair_color": "WHITE",
    "location": {
        "id": {{location_id}}
    },
    "birthday": "1990-01-01T00:00:00Z",
    "weight": 60,
    "nationality": "SPAIN"
}
HTTP 201
[Captures]
second_id: jsonpath "$.id"
[Asserts]
header "Location" matches "/api/v1/persons/\\d+$"

GET {{base_url}}/persons/{{second_id}}
HTTP 200
[Asserts]
jsonpath "$.location.id" == {{location_id}}
jsonpath "$.eye_color" == "BLUE"

PUT {{base_url}}/persons/{{id}}
X-CSRF-Token: {{csrf}}
{
    "eye_color": "ORANGE",
    "hair_color": "BROWN",
    "location": {
        "x": 7,
        "y": 8,
        "z": 9
    },
    "birthday": "2001-02-03T04:05:06Z",
    "weight": 80.25,
    "nationality": "ITALY"
}
HTTP 204

GET {{base_url}}/persons/{{id}}
HTTP 200
[Asserts]
jsonpath "$.eye_color" == "ORANGE"
jsonpath "$.hair_color" == "BROWN"
jsonpath "$.location.id" != {{location_id}}
jsonpath "$.location.x" == 7
jsonpath "$.birthday" == "2001-02-03T04:05:06Z"
jsonpath "$.weight" == 80.25
jsonpath "$.nationality" == "ITALY"

DELETE {{base_url}}/persons/{{second_id}}
X-CSRF-Token: {{csrf}}
HTTP 204

GET {{base_url}}/persons/{{second_id}}
HTTP 404
[Asserts]
jsonpath "$.message" == "Person with id={{second_id}} does not exist."

GET {{base_url}}/locations/{{location_id}}
HTTP 200

DELETE {{base_url}}/persons/{{second_id}}
X-CSRF-Token: {{csrf}}
HTTP 404
