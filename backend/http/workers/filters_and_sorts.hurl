GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "worker_filter_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "worker_filter_user",
    "password": "secret_1"
}
HTTP 200

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

GET {{base_url}}/workers
HTTP 200
[Asserts]
jsonpath "$.page" == 1
jsonpath "$.page_size" == 20
jsonpath "$.total_items" == 4
jsonpath "$.items[0].id" == 1
jsonpath "$.items[0].organization.full_name" == "ООО Магистраль"
jsonpath "$.items[0].person.hair_color" == "BROWN"
jsonpath "$.items[1].person" == null
jsonpath "$.items[2].organization" == null

GET {{base_url}}/workers?name=ANNA
HTTP 200
[Asserts]
jsonpath "$.total_items" == 2
jsonpath "$.items" count == 2
jsonpath "$.items[0].id" == 1
jsonpath "$.items[1].id" == 2

GET {{base_url}}/workers?name=_
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1
jsonpath "$.items" count == 1
jsonpath "$.items[0].id" == 2

GET {{base_url}}/workers?name=ANNA&status=hired
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1
jsonpath "$.items" count == 1
jsonpath "$.items[0].id" == 1

GET {{base_url}}/workers?status=reg
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1
jsonpath "$.items" count == 1
jsonpath "$.items[0].id" == 2

GET {{base_url}}/workers?position=dev
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1
jsonpath "$.items" count == 1
jsonpath "$.items[0].id" == 3

GET {{base_url}}/workers?position=xyz
HTTP 200
[Asserts]
jsonpath "$.total_items" == 0
jsonpath "$.items" count == 0

GET {{base_url}}/workers?name=bor
HTTP 200
[Asserts]
jsonpath "$.total_items" == 1
jsonpath "$.items[0].id" == 3

GET {{base_url}}/workers?sort_by=name&sort_order=desc
HTTP 200
[Asserts]
jsonpath "$.items" count == 4
jsonpath "$.items[0].id" == 4
jsonpath "$.items[1].id" == 3

GET {{base_url}}/workers?sort_by=name
HTTP 200
[Asserts]
jsonpath "$.items[2].id" == 3
jsonpath "$.items[3].id" == 4

GET {{base_url}}/workers?sort_by=status
HTTP 200
[Asserts]
jsonpath "$.items[0].id" == 1
jsonpath "$.items[1].id" == 4
jsonpath "$.items[2].id" == 2
jsonpath "$.items[3].id" == 3

GET {{base_url}}/workers?sort_by=salary&sort_order=desc
HTTP 200
[Asserts]
jsonpath "$.total_items" == 4
jsonpath "$.items" count == 4
jsonpath "$.items[0].id" == 2
jsonpath "$.items[1].id" == 4
jsonpath "$.items[2].id" == 3
jsonpath "$.items[3].id" == 1

GET {{base_url}}/workers?sort_by=organization_full_name
HTTP 400
[Asserts]
jsonpath "$.message" == "Parameter sort_by has an unsupported value."

GET {{base_url}}/workers?page=2&page_size=3
HTTP 200
[Asserts]
jsonpath "$.page" == 2
jsonpath "$.page_size" == 3
jsonpath "$.total_items" == 4
jsonpath "$.items" count == 1
jsonpath "$.items[0].id" == 4

GET {{base_url}}/workers?page=3&page_size=3
HTTP 200
[Asserts]
jsonpath "$.total_items" == 4
jsonpath "$.items" count == 0

GET {{base_url}}/workers?sort_by=creator_id
HTTP 400
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.message" == "Parameter sort_by has an unsupported value."

GET {{base_url}}/workers?page_size=101
HTTP 400
[Asserts]
jsonpath "$.message" == "Parameter page_size must not exceed 100."
