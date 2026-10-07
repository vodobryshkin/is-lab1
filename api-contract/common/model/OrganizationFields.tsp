import "./AddressFields.tsp";
import "../enum/OrganizationType.tsp";

namespace WorkersApi.Domain;

const organizationBaseExample = #{
    annual_turnover: 1000000,
    employees_count: 3,
    full_name: "ООО ЛТК Магистраль",
    rating: 1000,
    type: OrganizationType.OPEN_JOINT_STOCK_COMPANY
};

const organizationExample: OrganizationFields = #{
    ...organizationBaseExample,
    official_address: addressExample,
    postal_address: addressExample
};

@doc("Данные организации.")
@example(organizationExample)
model OrganizationFields {
    @doc("Официальный адрес организации.")
    official_address: AddressFields;

    @doc("Годовой оборот организации.")
    @minValueExclusive(0)
    annual_turnover: float32;

    @doc("Количество сотрудников.")
    @minValueExclusive(0)
    employees_count: int64;

    @doc("Полное название организации. Должно быть уникальным в БД.")
    @minLength(1)
    @pattern("^.+$")
    full_name: string;

    @doc("Рейтинг организации.")
    @minValueExclusive(0)
    rating: int32;

    @doc("Тип организации.")
    type: OrganizationType | null;

    @doc("Почтовый адрес организации.")
    postal_address: AddressFields;
}