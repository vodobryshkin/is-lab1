import { useState } from 'react'
import type { FormEvent } from 'react'
import type { SerializedError } from '@reduxjs/toolkit'
import type { FetchBaseQueryError } from '@reduxjs/toolkit/query/react'
import {
  useCreateLocationMutation,
  useDeleteLocationMutation,
  useListLocationsQuery,
  useUpdateLocationMutation,
} from '../api/api'
import type { Location, LocationFields } from '../api/api'
import { errorText, validationIssues } from '../api/errors'
import Issues from '../components/Issues'
import Modal from '../components/Modal'
import Pager from '../components/Pager'
import SortControls from '../components/SortControls'

interface FormProps {
  initial: { x: string; y: string; z: string }
  submitTitle: string
  isLoading: boolean
  error: FetchBaseQueryError | SerializedError | undefined
  onSubmit: (body: LocationFields) => void
}

function LocationForm({ initial, submitTitle, isLoading, error, onSubmit }: FormProps) {
  const [value, setValue] = useState(initial)
  const issues = validationIssues(error)

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    onSubmit({ x: Number(value.x), y: Number(value.y), z: Number(value.z) })
  }

  return (
    <form onSubmit={handleSubmit} className="worker-form">
      {error && <p role="alert">{errorText(error)}</p>}
      <fieldset disabled={isLoading}>
        <label>
          X (целое)
          <input
            type="number"
            step={1}
            min={-2147483648}
            max={2147483647}
            required
            value={value.x}
            onChange={(event) => setValue({ ...value, x: event.target.value })}
          />
        </label>
        <Issues issues={issues} field="x" />
        <label>
          Y (целое)
          <input
            type="number"
            step={1}
            min={-2147483648}
            max={2147483647}
            required
            value={value.y}
            onChange={(event) => setValue({ ...value, y: event.target.value })}
          />
        </label>
        <Issues issues={issues} field="y" />
        <label>
          Z (целое)
          <input
            type="number"
            step={1}
            required
            value={value.z}
            onChange={(event) => setValue({ ...value, z: event.target.value })}
          />
        </label>
        <Issues issues={issues} field="z" />
        <button type="submit">{isLoading ? 'Сохранение…' : submitTitle}</button>
      </fieldset>
    </form>
  )
}

function LocationsPage() {
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [sortBy, setSortBy] = useState('id')
  const [sortOrder, setSortOrder] = useState('asc')
  const [editing, setEditing] = useState<Location | null>(null)
  const [createdId, setCreatedId] = useState<number | null>(null)
  const [formKey, setFormKey] = useState(0)

  const list = useListLocationsQuery({ page, page_size: pageSize, sort_by: sortBy, sort_order: sortOrder })
  const [create, createState] = useCreateLocationMutation()
  const [update, updateState] = useUpdateLocationMutation()
  const [remove, removeState] = useDeleteLocationMutation()

  async function handleCreate(body: LocationFields) {
    const result = await create(body)
    if (result.data) {
      setCreatedId(result.data.id)
      setFormKey(formKey + 1)
    }
  }

  async function handleUpdate(body: LocationFields) {
    if (editing && !(await update({ id: editing.id, body })).error) {
      setEditing(null)
    }
  }

  function startEdit(item: Location) {
    updateState.reset()
    setEditing(item)
  }

  function handleDelete(item: Location) {
    if (window.confirm(`Удалить локацию с ID ${item.id}? Это действие нельзя отменить.`)) {
      remove(item.id)
    }
  }

  return (
    <>
      <h2>Локации</h2>

      <h3>Новая локация</h3>
      {createdId !== null && <p>Локация создана, ID {createdId}.</p>}
      <LocationForm
        key={formKey}
        initial={{ x: '', y: '', z: '' }}
        submitTitle="Создать"
        isLoading={createState.isLoading}
        error={createState.error}
        onSubmit={handleCreate}
      />

      <h3>Все локации</h3>
      <SortControls
        fields={[
          ['id', 'ID'],
          ['x', 'X'],
          ['y', 'Y'],
          ['z', 'Z'],
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
      {list.data && list.data.items.length === 0 && <p>Локаций нет.</p>}
      {list.data && list.data.items.length > 0 && (
        <div className="table-scroll">
          <table>
            <thead>
              <tr>
                <th></th>
                <th>ID</th>
                <th>X</th>
                <th>Y</th>
                <th>Z</th>
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
                  <td>{item.x}</td>
                  <td>{item.y}</td>
                  <td>{item.z}</td>
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
        <Modal title={`Изменение локации с ID ${editing.id}`} onClose={() => setEditing(null)}>
          <LocationForm
            initial={{ x: String(editing.x), y: String(editing.y), z: String(editing.z) }}
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

export default LocationsPage
