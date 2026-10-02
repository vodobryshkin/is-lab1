import "./Address.tsp";
import "../enum/OrganizationType.tsp";

namespace WorkersApi.Domain;

@doc("Данные, которые пользователь передает при создании организации.")
model Organization {
    official_address: Address;

    annual_turnover: float32;

    employees_count: int64;

    full_name: string;

    rating: int32;

    type: OrganizationType;

    postal_address: Address;
}