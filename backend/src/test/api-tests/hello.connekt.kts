val host = "http://localhost:8080"

GET("$host/backend/api/hello-world") {
    header("Accept", "text/plain")
} then {
    val bodyValue = body?.string()
    assert(bodyValue == "Hello, World!")
}