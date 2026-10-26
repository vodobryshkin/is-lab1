GET {{base_url}}/workers/count_by_status?status=HIRED
HTTP 401

GET {{base_url}}/workers/count_by_status_greater_than?status=HIRED
HTTP 401

GET {{base_url}}/workers/rating_less_than?rating=10
HTTP 401

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "worker_special_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "worker_special_user",
    "password": "secret_1"
}
HTTP 200

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

GET {{base_url}}/workers/count_by_status?status=HIRED
HTTP 200
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.count" == 2

GET {{base_url}}/workers/count_by_status?status=RECOMMENDED_FOR_PROMOTION
HTTP 200
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.count" == 1

GET {{base_url}}/workers/count_by_status?status=REGULAR
HTTP 200
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.count" == 1

GET {{base_url}}/workers/count_by_status?status=null
HTTP 200
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.count" == 1

GET {{base_url}}/workers/count_by_status?status=hired
HTTP 400
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.status" == 400
jsonpath "$.errors[0].field" == "status"
jsonpath "$.errors[0].title" == "INVALID_ENUM_VALUE"

GET {{base_url}}/workers/count_by_status
HTTP 400
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.status" == 400
jsonpath "$.errors[0].field" == "status"
jsonpath "$.errors[0].title" == "REQUIRED"

GET {{base_url}}/workers/count_by_status_greater_than?status=HIRED
HTTP 200
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.count" == 2

GET {{base_url}}/workers/count_by_status_greater_than?status=RECOMMENDED_FOR_PROMOTION
HTTP 200
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.count" == 1

GET {{base_url}}/workers/count_by_status_greater_than?status=REGULAR
HTTP 200
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.count" == 0

GET {{base_url}}/workers/count_by_status_greater_than?status=null
HTTP 400
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.status" == 400
jsonpath "$.errors[0].field" == "status"
jsonpath "$.errors[0].title" == "INVALID_ENUM_VALUE"

GET {{base_url}}/workers/count_by_status_greater_than
HTTP 400
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.status" == 400
jsonpath "$.errors[0].field" == "status"
jsonpath "$.errors[0].title" == "REQUIRED"

GET {{base_url}}/workers/rating_less_than?rating=10
HTTP 200
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$" count == 2
jsonpath "$[0].id" == 1
jsonpath "$[0].rating" == 5
jsonpath "$[0].organization.full_name" == "Employer"
jsonpath "$[0].person.hair_color" == "BROWN"
jsonpath "$[1].id" == 2
jsonpath "$[1].rating" == 1
jsonpath "$[1].organization" == null

GET {{base_url}}/workers/rating_less_than?rating=1
HTTP 200
[Asserts]
jsonpath "$" count == 0

GET {{base_url}}/workers/rating_less_than?rating=-5
HTTP 200
[Asserts]
jsonpath "$" count == 0

GET {{base_url}}/workers/rating_less_than?rating=2147483647
HTTP 200
[Asserts]
jsonpath "$" count == 4
jsonpath "$[0].id" == 1
jsonpath "$[1].id" == 2
jsonpath "$[2].id" == 3
jsonpath "$[3].id" == 4

GET {{base_url}}/workers/rating_less_than?rating=abc
HTTP 400
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.status" == 400
jsonpath "$.errors[0].field" == "rating"
jsonpath "$.errors[0].title" == "INVALID_TYPE"

GET {{base_url}}/workers/rating_less_than?rating=1.5
HTTP 400
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.status" == 400
jsonpath "$.errors[0].field" == "rating"
jsonpath "$.errors[0].title" == "INVALID_TYPE"

GET {{base_url}}/workers/rating_less_than?rating=2147483648
HTTP 400
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.status" == 400
jsonpath "$.errors[0].field" == "rating"
jsonpath "$.errors[0].title" == "OUT_OF_RANGE"

GET {{base_url}}/workers/rating_less_than
HTTP 400
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.type" == "/problems/validation-failed"
jsonpath "$.status" == 400
jsonpath "$.errors[0].field" == "rating"
jsonpath "$.errors[0].title" == "REQUIRED"
