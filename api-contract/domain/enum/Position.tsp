namespace WorkersApi.Domain;

@doc("Допустимые позиции, которые может занимать сотрудник.")
enum Position {
    DIRECTOR: "DIRECTOR",
    HEAD_OF_DIVISION: "HEAD_OF_DIVISION",
    LEAD_DEVELOPER: "LEAD_DEVELOPER"
}