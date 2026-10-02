import "./AddressFields.tsp";
import "../enum/OrganizationType.tsp";

namespace WorkersApi.Domain;

const organizationExample: OrganizationFields = #{
    official_address: addressExample,
    annual_turnover: 1000000,
    employees_count: 3,
    full_name: "ООО ЛТК Магистраль",
    rating: 1000,
    type: OrganizationType.OPEN_JOINT_STOCK_COMPANY,
    postal_address: addressExample
};

@doc("Данные, которые пользователь передает при создании организации.")
@example(organizationExample)
model OrganizationFields {
    @doc("Официальный адрес организации.")
    official_address: AddressFields;

    @doc("Годовой оборот организации.")
    @minValueExclusive(0)
    annual_turnover: float32;

    @doc("Количество сотрудников в организации.")
    @minValueExclusive(0)
    employees_count: int64;

    @doc("Полное юридическое название организации.")
    @pattern("^.+$")
    full_name: string;

    @doc("Рейтинг организации.")
    @minValueExclusive(0)
    rating: int32;

    @doc("Тип организации.")
    type: OrganizationType | null;

    @doc("Почтовый адрес компании.")
    postal_address: AddressFields;
}