namespace WorkersApi.Domain;

@doc("Допустимые значения статуса работника.")
enum Status {
    HIRED: "HIRED",
    RECOMMENDED_FOR_PROMOTION: "RECOMMENDED_FOR_PROMOTION",
    REGULAR: "REGULAR"
}