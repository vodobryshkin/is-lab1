import "./Address.tsp";
import "../enum/OrganizationType.tsp";

namespace WorkersApi.Domain;

@doc("Данные, которые пользователь передает при создании организации.")
model Organization {
    official_address: Address;

    @minValueExclusive(0)
    annual_turnover: float32;

    @minValueExclusive(0)
    employees_count: int64;

    @pattern("^.+$")
    full_name: string;

    @minValueExclusive(0)
    rating: int32;

    type: OrganizationType | null;

    postal_address: Address;
}