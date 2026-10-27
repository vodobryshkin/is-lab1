import { colors, countries } from '../api/api'
import type { ValidationIssue } from '../api/errors'
import type { PersonState } from '../forms'
import Issues from './Issues'
import LocationInput from './LocationInput'

interface Props {
  value: PersonState
  onChange: (value: PersonState) => void
  issues: ValidationIssue[]
  // Путь полей в ошибках 422: "person" внутри работника, "" для самой персоны.
  field: string
}

function PersonFields({ value, onChange, issues, field }: Props) {
  const path = (name: string) => (field ? `${field}.${name}` : name)

  return (
    <>
      <label>
        Цвет глаз
        <select value={value.eyeColor} onChange={(event) => onChange({ ...value, eyeColor: event.target.value })}>
          <option value="">не указан</option>
          {colors.map((color) => (
            <option key={color}>{color}</option>
          ))}
        </select>
      </label>
      <Issues issues={issues} field={path('eye_color')} />
      <label>
        Цвет волос
        <select value={value.hairColor} onChange={(event) => onChange({ ...value, hairColor: event.target.value })}>
          {colors.map((color) => (
            <option key={color}>{color}</option>
          ))}
        </select>
      </label>
      <Issues issues={issues} field={path('hair_color')} />
      <label>
        Дата рождения
        <input
          type="date"
          required
          value={value.birthday}
          onChange={(event) => onChange({ ...value, birthday: event.target.value })}
        />
      </label>
      <Issues issues={issues} field={path('birthday')} />
      <label>
        Вес (больше 0)
        <input
          type="number"
          step="any"
          min={0}
          required
          value={value.weight}
          onChange={(event) => onChange({ ...value, weight: event.target.value })}
        />
      </label>
      <Issues issues={issues} field={path('weight')} />
      <label>
        Национальность
        <select value={value.nationality} onChange={(event) => onChange({ ...value, nationality: event.target.value })}>
          {countries.map((country) => (
            <option key={country}>{country}</option>
          ))}
        </select>
      </label>
      <Issues issues={issues} field={path('nationality')} />
      <LocationInput
        legend="Местоположение"
        value={value.location}
        onChange={(location) => onChange({ ...value, location })}
        allowNone={false}
        issues={issues}
        field={path('location')}
      />
    </>
  )
}

export default PersonFields
