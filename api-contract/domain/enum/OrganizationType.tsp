namespace WorkersApi.Domain;

@doc("Допустимые значения типа организации.")
enum OrganizationType {
    PUBLIC: "PUBLIC",
    GOVERNMENT: "GOVERNMENT",
    TRUST: "TRUST",
    PRIVATE_LIMITED_COMPANY: "PRIVATE_LIMITED_COMPANY",
    OPEN_JOINT_STOCK_COMPANY: "OPEN_JOINT_STOCK_COMPANY"
}