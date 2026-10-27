import { organizationTypes } from '../api/api'
import type { ValidationIssue } from '../api/errors'
import type { OrganizationState } from '../forms'
import AddressInput from './AddressInput'
import Issues from './Issues'

interface Props {
  value: OrganizationState
  onChange: (value: OrganizationState) => void
  issues: ValidationIssue[]
  // Путь полей в ошибках 422: "organization" внутри работника, "" для самой организации.
  field: string
}

function OrganizationFields({ value, onChange, issues, field }: Props) {
  const path = (name: string) => (field ? `${field}.${name}` : name)

  return (
    <>
      <label>
        Полное название (уникальное)
        <input
          type="text"
          required
          pattern=".*\S.*"
          title="Название не может быть пустым"
          value={value.fullName}
          onChange={(event) => onChange({ ...value, fullName: event.target.value })}
        />
      </label>
      <Issues issues={issues} field={path('full_name')} />
      <label>
        Годовой оборот (больше 0)
        <input
          type="number"
          step="any"
          min={0}
          required
          value={value.annualTurnover}
          onChange={(event) => onChange({ ...value, annualTurnover: event.target.value })}
        />
      </label>
      <Issues issues={issues} field={path('annual_turnover')} />
      <label>
        Количество сотрудников (больше 0)
        <input
          type="number"
          step={1}
          min={1}
          required
          value={value.employeesCount}
          onChange={(event) => onChange({ ...value, employeesCount: event.target.value })}
        />
      </label>
      <Issues issues={issues} field={path('employees_count')} />
      <label>
        Рейтинг (целое, больше 0)
        <input
          type="number"
          step={1}
          min={1}
          max={2147483647}
          required
          value={value.rating}
          onChange={(event) => onChange({ ...value, rating: event.target.value })}
        />
      </label>
      <Issues issues={issues} field={path('rating')} />
      <label>
        Тип
        <select value={value.type} onChange={(event) => onChange({ ...value, type: event.target.value })}>
          <option value="">не указан</option>
          {organizationTypes.map((type) => (
            <option key={type}>{type}</option>
          ))}
        </select>
      </label>
      <Issues issues={issues} field={path('type')} />
      <AddressInput
        legend="Официальный адрес"
        value={value.official}
        onChange={(official) => onChange({ ...value, official })}
        issues={issues}
        field={path('official_address')}
      />
      <AddressInput
        legend="Почтовый адрес"
        value={value.postal}
        onChange={(postal) => onChange({ ...value, postal })}
        issues={issues}
        field={path('postal_address')}
      />
    </>
  )
}

export default OrganizationFields
