import { choiceParams, useListAddressesQuery } from '../api/api'
import type { ValidationIssue } from '../api/errors'
import type { AddressState } from '../forms'
import IdInput from './IdInput'
import Issues from './Issues'
import LocationInput from './LocationInput'

interface Props {
  legend: string
  value: AddressState
  onChange: (value: AddressState) => void
  issues: ValidationIssue[]
  field: string
}

function AddressInput({ legend, value, onChange, issues, field }: Props) {
  const addresses = useListAddressesQuery(choiceParams, { skip: value.mode !== 'existing' })
  const choices = (addresses.data?.items ?? []).map((address) => ({ id: address.id, title: address.street }))

  return (
    <fieldset>
      <legend>{legend}</legend>
      <div className="radio-group">
        <label>
          <input type="radio" checked={value.mode === 'new'} onChange={() => onChange({ ...value, mode: 'new' })} />
          Новый
        </label>
        <label>
          <input
            type="radio"
            checked={value.mode === 'existing'}
            onChange={() => onChange({ ...value, mode: 'existing' })}
          />
          Существующий
        </label>
      </div>
      <Issues issues={issues} field={field} />

      {value.mode === 'new' && (
        <>
          <label>
            Улица
            <input
              type="text"
              required
              pattern=".*\S.*"
              title="Улица не может быть пустой"
              value={value.street}
              onChange={(event) => onChange({ ...value, street: event.target.value })}
            />
          </label>
          <Issues issues={issues} field={`${field}.street`} />
          <LocationInput
            legend="Город"
            value={value.town}
            onChange={(town) => onChange({ ...value, town })}
            allowNone={true}
            issues={issues}
            field={`${field}.town`}
          />
        </>
      )}

      {value.mode === 'existing' && (
        <IdInput
          label="ID адреса"
          value={value.id}
          onChange={(id) => onChange({ ...value, id })}
          choices={choices}
          issues={issues}
          field={`${field}_id`}
        />
      )}
    </fieldset>
  )
}

export default AddressInput
