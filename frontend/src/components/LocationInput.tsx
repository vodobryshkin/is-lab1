import { choiceParams, useListLocationsQuery } from '../api/api'
import type { ValidationIssue } from '../api/errors'
import type { LocationState } from '../forms'
import IdInput from './IdInput'
import Issues from './Issues'

interface Props {
  legend: string
  value: LocationState
  onChange: (value: LocationState) => void
  allowNone: boolean
  issues: ValidationIssue[]
  field: string
}

function LocationInput({ legend, value, onChange, allowNone, issues, field }: Props) {
  const locations = useListLocationsQuery(choiceParams, { skip: value.mode !== 'existing' })
  const choices = (locations.data?.items ?? []).map((location) => ({
    id: location.id,
    title: `(${location.x}; ${location.y}; ${location.z})`,
  }))

  return (
    <fieldset>
      <legend>{legend}</legend>
      <div className="radio-group">
        {allowNone && (
          <label>
            <input type="radio" checked={value.mode === 'none'} onChange={() => onChange({ ...value, mode: 'none' })} />
            Не указана
          </label>
        )}
        <label>
          <input type="radio" checked={value.mode === 'new'} onChange={() => onChange({ ...value, mode: 'new' })} />
          Новая
        </label>
        <label>
          <input
            type="radio"
            checked={value.mode === 'existing'}
            onChange={() => onChange({ ...value, mode: 'existing' })}
          />
          Существующая
        </label>
      </div>
      <Issues issues={issues} field={field} />

      {value.mode === 'new' && (
        <>
          <label>
            X (целое)
            <input
              type="number"
              step={1}
              min={-2147483648}
              max={2147483647}
              required
              value={value.x}
              onChange={(event) => onChange({ ...value, x: event.target.value })}
            />
          </label>
          <Issues issues={issues} field={`${field}.x`} />
          <label>
            Y (целое)
            <input
              type="number"
              step={1}
              min={-2147483648}
              max={2147483647}
              required
              value={value.y}
              onChange={(event) => onChange({ ...value, y: event.target.value })}
            />
          </label>
          <Issues issues={issues} field={`${field}.y`} />
          <label>
            Z (целое)
            <input
              type="number"
              step={1}
              required
              value={value.z}
              onChange={(event) => onChange({ ...value, z: event.target.value })}
            />
          </label>
          <Issues issues={issues} field={`${field}.z`} />
        </>
      )}

      {value.mode === 'existing' && (
        <IdInput
          label="ID локации"
          value={value.id}
          onChange={(id) => onChange({ ...value, id })}
          choices={choices}
          issues={issues}
          field={`${field}.id`}
        />
      )}
    </fieldset>
  )
}

export default LocationInput
