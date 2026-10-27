GET {{base_url}}/events
Connection: Upgrade
Upgrade: websocket
Sec-WebSocket-Version: 13
Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==
Origin: http://localhost:5173
HTTP 401
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.message" == "Log in to access this resource."

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

POST {{base_url}}/auth/register
X-CSRF-Token: {{csrf}}
{
    "username": "events_upgrade_user",
    "password": "secret_1"
}
HTTP 201

POST {{base_url}}/auth/login
X-CSRF-Token: {{csrf}}
{
    "username": "events_upgrade_user",
    "password": "secret_1"
}
HTTP 200

GET {{base_url}}/auth/csrf
HTTP 200
[Captures]
csrf: jsonpath "$.csrf_token"

GET {{base_url}}/events
Connection: Upgrade
Upgrade: websocket
Sec-WebSocket-Version: 13
Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==
HTTP 403
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.message" == "The Origin header is missing or not allowed."

GET {{base_url}}/events
Connection: Upgrade
Upgrade: websocket
Sec-WebSocket-Version: 13
Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==
Origin: http://evil.example
HTTP 403
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.message" == "The Origin header is missing or not allowed."

GET {{base_url}}/events?token=secret
Connection: Upgrade
Upgrade: websocket
Sec-WebSocket-Version: 13
Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==
Origin: http://localhost:5173
HTTP 400
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.message" == "URL parameters are not supported."

POST {{base_url}}/auth/logout
X-CSRF-Token: {{csrf}}
HTTP 204

GET {{base_url}}/events
Connection: Upgrade
Upgrade: websocket
Sec-WebSocket-Version: 13
Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==
Origin: http://localhost:5173
HTTP 401
[Asserts]
header "Content-Type" contains "application/json"
jsonpath "$.message" == "Log in to access this resource."
