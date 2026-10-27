import { useState } from 'react'
import type { FormEvent } from 'react'
import type { SerializedError } from '@reduxjs/toolkit'
import type { FetchBaseQueryError } from '@reduxjs/toolkit/query/react'
import {
  useCreatePersonMutation,
  useDeletePersonMutation,
  useListPersonsQuery,
  useUpdatePersonMutation,
} from '../api/api'
import type { Person, PersonWriteRequest } from '../api/api'
import { errorText, validationIssues } from '../api/errors'
import Modal from '../components/Modal'
import Pager from '../components/Pager'
import PersonFields from '../components/PersonFields'
import SortControls from '../components/SortControls'
import { emptyPerson, personRequest, personStateFrom } from '../forms'
import type { PersonState } from '../forms'
import { formatLocation, formatValue } from '../format'

interface FormProps {
  initial: PersonState
  submitTitle: string
  isLoading: boolean
  error: FetchBaseQueryError | SerializedError | undefined
  onSubmit: (body: PersonWriteRequest) => void
}

function PersonForm({ initial, submitTitle, isLoading, error, onSubmit }: FormProps) {
  const [value, setValue] = useState(initial)

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    onSubmit(personRequest(value))
  }

  return (
    <form onSubmit={handleSubmit} className="worker-form">
      {error && <p role="alert">{errorText(error)}</p>}
      <fieldset disabled={isLoading}>
        <PersonFields value={value} onChange={setValue} issues={validationIssues(error)} field="" />
        <button type="submit">{isLoading ? 'Сохранение…' : submitTitle}</button>
      </fieldset>
    </form>
  )
}

const emptyFilters = { eye_color: '', hair_color: '', nationality: '' }

function PersonsPage() {
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [sortBy, setSortBy] = useState('id')
  const [sortOrder, setSortOrder] = useState('asc')
  const [filterInputs, setFilterInputs] = useState(emptyFilters)
  const [filters, setFilters] = useState(emptyFilters)
  const [editing, setEditing] = useState<Person | null>(null)
  const [createdId, setCreatedId] = useState<number | null>(null)
  const [formKey, setFormKey] = useState(0)

  const list = useListPersonsQuery({
    page,
    page_size: pageSize,
    sort_by: sortBy,
    sort_order: sortOrder,
    eye_color: filters.eye_color || undefined,
    hair_color: filters.hair_color || undefined,
    nationality: filters.nationality || undefined,
  })
  const [create, createState] = useCreatePersonMutation()
  const [update, updateState] = useUpdatePersonMutation()
  const [remove, removeState] = useDeletePersonMutation()

  async function handleCreate(body: PersonWriteRequest) {
    const result = await create(body)
    if (result.data) {
      setCreatedId(result.data.id)
      setFormKey(formKey + 1)
    }
  }

  async function handleUpdate(body: PersonWriteRequest) {
    if (editing && !(await update({ id: editing.id, body })).error) {
      setEditing(null)
    }
  }

  function startEdit(item: Person) {
    updateState.reset()
    setEditing(item)
  }

  function handleDelete(item: Person) {
    if (window.confirm(`Удалить персону с ID ${item.id}? Это действие нельзя отменить.`)) {
      remove(item.id)
    }
  }

  function applyFilters(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setFilters({
      eye_color: filterInputs.eye_color.trim(),
      hair_color: filterInputs.hair_color.trim(),
      nationality: filterInputs.nationality.trim(),
    })
    setPage(1)
  }

  return (
    <>
      <h2>Персоны</h2>

      <h3>Новая персона</h3>
      <p>Персона создаётся без работника. Привязать её можно в форме работника, выбрав существующую персону.</p>
      {createdId !== null && <p>Персона создана, ID {createdId}.</p>}
      <PersonForm
        key={formKey}
        initial={emptyPerson}
        submitTitle="Создать"
        isLoading={createState.isLoading}
        error={createState.error}
        onSubmit={handleCreate}
      />

      <h3>Все персоны</h3>
      <form onSubmit={applyFilters} className="inline-form">
        <label>
          Цвет глаз содержит
          <input
            type="text"
            placeholder="например, BLU"
            value={filterInputs.eye_color}
            onChange={(event) => setFilterInputs({ ...filterInputs, eye_color: event.target.value })}
          />
        </label>
        <label>
          Цвет волос содержит
          <input
            type="text"
            value={filterInputs.hair_color}
            onChange={(event) => setFilterInputs({ ...filterInputs, hair_color: event.target.value })}
          />
        </label>
        <label>
          Национальность содержит
          <input
            type="text"
            placeholder="например, RUS"
            value={filterInputs.nationality}
            onChange={(event) => setFilterInputs({ ...filterInputs, nationality: event.target.value })}
          />
        </label>
        <button type="submit">Найти</button>
        <button
          type="button"
          onClick={() => {
            setFilterInputs(emptyFilters)
            setFilters(emptyFilters)
            setPage(1)
          }}
        >
          Сбросить
        </button>
      </form>
      <SortControls
        fields={[
          ['id', 'ID'],
          ['eye_color', 'Цвет глаз'],
          ['hair_color', 'Цвет волос'],
          ['nationality', 'Национальность'],
          ['birthday', 'Дата рождения'],
          ['weight', 'Вес'],
        ]}
        sortBy={sortBy}
        sortOrder={sortOrder}
        onChange={(by, order) => {
          setSortBy(by)
          setSortOrder(order)
          setPage(1)
        }}
      />
      {removeState.isError && <p role="alert">{errorText(removeState.error)}</p>}
      {list.isLoading && <p>Загрузка…</p>}
      {list.isError && <p role="alert">{errorText(list.error)}</p>}
      {list.data && list.data.items.length === 0 && <p>Персоны не найдены.</p>}
      {list.data && list.data.items.length > 0 && (
        <div className="table-scroll">
          <table>
            <thead>
              <tr>
                <th></th>
                <th>ID</th>
                <th>Цвет глаз</th>
                <th>Цвет волос</th>
                <th>Местоположение</th>
                <th>Дата рождения</th>
                <th>Вес</th>
                <th>Национальность</th>
              </tr>
            </thead>
            <tbody>
              {list.data.items.map((item) => (
                <tr key={item.id}>
                  <td>
                    <div className="row-actions">
                      <button type="button" onClick={() => startEdit(item)}>
                        Изменить
                      </button>
                      <button type="button" onClick={() => handleDelete(item)} disabled={removeState.isLoading}>
                        Удалить
                      </button>
                    </div>
                  </td>
                  <td>{item.id}</td>
                  <td>{formatValue(item.eye_color)}</td>
                  <td>{item.hair_color}</td>
                  <td>
                    {formatLocation(item.location)}, ID {item.location.id}
                  </td>
                  <td>{new Date(item.birthday).toLocaleDateString('ru-RU')}</td>
                  <td>{item.weight}</td>
                  <td>{item.nationality}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      {list.data && (
        <Pager
          page={page}
          pageSize={pageSize}
          totalItems={list.data.total_items}
          isFetching={list.isFetching}
          onPage={setPage}
          onPageSize={(size) => {
            setPageSize(size)
            setPage(1)
          }}
        />
      )}

      {editing && (
        <Modal title={`Изменение персоны с ID ${editing.id}`} onClose={() => setEditing(null)}>
          <PersonForm
            initial={personStateFrom(editing)}
            submitTitle="Сохранить"
            isLoading={updateState.isLoading}
            error={updateState.error}
            onSubmit={handleUpdate}
          />
          <button type="button" onClick={() => setEditing(null)}>
            Отмена
          </button>
        </Modal>
      )}
    </>
  )
}

export default PersonsPage
