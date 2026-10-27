import { useState } from 'react'
import type { FormEvent } from 'react'
import type { SerializedError } from '@reduxjs/toolkit'
import type { FetchBaseQueryError } from '@reduxjs/toolkit/query/react'
import {
  choiceParams,
  positions,
  statuses,
  useListCoordinatesQuery,
  useListOrganizationsQuery,
  useListPersonsQuery,
} from '../api/api'
import { errorText, fieldLabel, validationIssues } from '../api/errors'
import IdInput from '../components/IdInput'
import Issues from '../components/Issues'
import OrganizationFields from '../components/OrganizationFields'
import PersonFields from '../components/PersonFields'
import type { WorkerState } from '../forms'

interface Props {
  initial: WorkerState
  forUpdate: boolean
  submitTitle: string
  isLoading: boolean
  error: FetchBaseQueryError | SerializedError | undefined
  onSubmit: (state: WorkerState) => void
  onCancel?: () => void
}

function WorkerForm({ initial, forUpdate, submitTitle, isLoading, error, onSubmit, onCancel }: Props) {
  const [state, setState] = useState(initial)
  const { coordinates, person, organization } = state
  const issues = validationIssues(error)

  const coordinatesList = useListCoordinatesQuery(choiceParams, { skip: coordinates.mode !== 'existing' })
  const personsList = useListPersonsQuery(choiceParams, { skip: person.mode !== 'existing' })
  const organizationsList = useListOrganizationsQuery(choiceParams, { skip: organization.mode !== 'existing' })

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    onSubmit(state)
  }

  return (
    <form onSubmit={handleSubmit} className="worker-form">
      {error && <p role="alert">{errorText(error)}</p>}
      {issues.length > 0 && (
        <ul>
          {issues.map((issue) => (
            <li key={issue.field + issue.code}>
              {fieldLabel(issue.field)}: {issue.message}
            </li>
          ))}
        </ul>
      )}

      <fieldset disabled={isLoading}>
        <fieldset>
          <legend>Работник</legend>
          <label>
            Имя
            <input
              type="text"
              required
              maxLength={255}
              pattern={"[\\p{L}\\d\\s_.'\"\\-]+"}
              title="Буквы, цифры, пробелы и символы _ . ' &quot; -"
              value={state.name}
              onChange={(event) => setState({ ...state, name: event.target.value })}
            />
          </label>
          <Issues issues={issues} field="name" />
          <label>
            Зарплата (больше 0)
            <input
              type="number"
              step="any"
              min={0}
              required
              value={state.salary}
              onChange={(event) => setState({ ...state, salary: event.target.value })}
            />
          </label>
          <Issues issues={issues} field="salary" />
          <label>
            Рейтинг (целое, больше 0)
            <input
              type="number"
              step={1}
              min={1}
              max={2147483647}
              required
              value={state.rating}
              onChange={(event) => setState({ ...state, rating: event.target.value })}
            />
          </label>
          <Issues issues={issues} field="rating" />
          <label>
            Дата начала работы
            <input
              type="datetime-local"
              required
              value={state.startDate}
              onChange={(event) => setState({ ...state, startDate: event.target.value })}
            />
          </label>
          <Issues issues={issues} field="start_date" />
          <label>
            Должность
            <select value={state.position} onChange={(event) => setState({ ...state, position: event.target.value })}>
              <option value="">не указана</option>
              {positions.map((value) => (
                <option key={value}>{value}</option>
              ))}
            </select>
          </label>
          <Issues issues={issues} field="position" />
          <label>
            Статус
            <select value={state.status} onChange={(event) => setState({ ...state, status: event.target.value })}>
              <option value="">не указан</option>
              {statuses.map((value) => (
                <option key={value}>{value}</option>
              ))}
            </select>
          </label>
          <Issues issues={issues} field="status" />
        </fieldset>

        <fieldset>
          <legend>Координаты</legend>
          <div className="radio-group">
            <label>
              <input
                type="radio"
                checked={coordinates.mode === 'new'}
                onChange={() => setState({ ...state, coordinates: { ...coordinates, mode: 'new' } })}
              />
              Новые
            </label>
            <label>
              <input
                type="radio"
                checked={coordinates.mode === 'existing'}
                onChange={() => setState({ ...state, coordinates: { ...coordinates, mode: 'existing' } })}
              />
              Существующие
            </label>
          </div>
          <Issues issues={issues} field="coordinates" />
          {coordinates.mode === 'new' && (
            <>
              <label>
                X (целое, больше -954)
                <input
                  type="number"
                  step={1}
                  min={-953}
                  max={2147483647}
                  required
                  value={coordinates.x}
                  onChange={(event) => setState({ ...state, coordinates: { ...coordinates, x: event.target.value } })}
                />
              </label>
              <Issues issues={issues} field="coordinates.x" />
              <label>
                Y (целое, больше -392)
                <input
                  type="number"
                  step={1}
                  min={-391}
                  required
                  value={coordinates.y}
                  onChange={(event) => setState({ ...state, coordinates: { ...coordinates, y: event.target.value } })}
                />
              </label>
              <Issues issues={issues} field="coordinates.y" />
            </>
          )}
          {coordinates.mode === 'existing' && (
            <IdInput
              label="ID координат"
              value={coordinates.id}
              onChange={(id) => setState({ ...state, coordinates: { ...coordinates, id } })}
              choices={(coordinatesList.data?.items ?? []).map((item) => ({
                id: item.id,
                title: `(${item.x}; ${item.y})`,
              }))}
              issues={issues}
              field="coordinates.id"
            />
          )}
        </fieldset>

        <fieldset>
          <legend>Персона</legend>
          <div className="radio-group">
            <label>
              <input
                type="radio"
                checked={person.mode === 'none'}
                onChange={() => setState({ ...state, person: { ...person, mode: 'none' } })}
              />
              Не указана
            </label>
            <label>
              <input
                type="radio"
                checked={person.mode === 'new'}
                onChange={() => setState({ ...state, person: { ...person, mode: 'new' } })}
              />
              Новая
            </label>
            <label>
              <input
                type="radio"
                checked={person.mode === 'existing'}
                onChange={() => setState({ ...state, person: { ...person, mode: 'existing' } })}
              />
              Существующая
            </label>
          </div>
          <Issues issues={issues} field="person" />
          {person.mode === 'new' && (
            <PersonFields
              value={person}
              onChange={(value) => setState({ ...state, person: { ...person, ...value } })}
              issues={issues}
              field="person"
            />
          )}
          {person.mode === 'existing' && (
            <IdInput
              label="ID персоны"
              value={person.id}
              onChange={(id) => setState({ ...state, person: { ...person, id } })}
              choices={(personsList.data?.items ?? []).map((item) => ({
                id: item.id,
                title: `${item.nationality}, ${item.hair_color}, ${new Date(item.birthday).toLocaleDateString('ru-RU')}`,
              }))}
              issues={issues}
              field="person.id"
            />
          )}
        </fieldset>

        <fieldset>
          <legend>Организация</legend>
          <div className="radio-group">
            <label>
              <input
                type="radio"
                checked={organization.mode === 'none'}
                onChange={() => setState({ ...state, organization: { ...organization, mode: 'none' } })}
              />
              Не указана
            </label>
            <label>
              <input
                type="radio"
                checked={organization.mode === 'existing'}
                onChange={() => setState({ ...state, organization: { ...organization, mode: 'existing' } })}
              />
              Существующая
            </label>
            {!forUpdate && (
              <label>
                <input
                  type="radio"
                  checked={organization.mode === 'new'}
                  onChange={() => setState({ ...state, organization: { ...organization, mode: 'new' } })}
                />
                Новая
              </label>
            )}
          </div>
          {forUpdate && <small>При изменении работника можно выбрать только существующую организацию.</small>}
          <Issues issues={issues} field="organization" />
          {organization.mode === 'existing' && (
            <IdInput
              label="ID организации"
              value={organization.id}
              onChange={(id) => setState({ ...state, organization: { ...organization, id } })}
              choices={(organizationsList.data?.items ?? []).map((item) => ({ id: item.id, title: item.full_name }))}
              issues={issues}
              field="organization_id"
            />
          )}
          {organization.mode === 'new' && (
            <OrganizationFields
              value={organization}
              onChange={(value) => setState({ ...state, organization: { ...organization, ...value } })}
              issues={issues}
              field="organization"
            />
          )}
        </fieldset>

        <button type="submit">{isLoading ? 'Сохранение…' : submitTitle}</button>
        {onCancel && (
          <button type="button" onClick={onCancel}>
            Отмена
          </button>
        )}
      </fieldset>
    </form>
  )
}

export default WorkerForm
