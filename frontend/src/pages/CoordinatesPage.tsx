import { useState } from 'react'
import type { FormEvent } from 'react'
import type { SerializedError } from '@reduxjs/toolkit'
import type { FetchBaseQueryError } from '@reduxjs/toolkit/query/react'
import {
  useCreateCoordinatesMutation,
  useDeleteCoordinatesMutation,
  useListCoordinatesQuery,
  useUpdateCoordinatesMutation,
} from '../api/api'
import type { Coordinates } from '../api/api'
import { errorText, validationIssues } from '../api/errors'
import Issues from '../components/Issues'
import Modal from '../components/Modal'
import Pager from '../components/Pager'
import SortControls from '../components/SortControls'

interface FormProps {
  initial: { x: string; y: string }
  submitTitle: string
  isLoading: boolean
  error: FetchBaseQueryError | SerializedError | undefined
  onSubmit: (body: { x: number; y: number }) => void
}

function CoordinatesForm({ initial, submitTitle, isLoading, error, onSubmit }: FormProps) {
  const [value, setValue] = useState(initial)
  const issues = validationIssues(error)

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    onSubmit({ x: Number(value.x), y: Number(value.y) })
  }

  return (
    <form onSubmit={handleSubmit} className="worker-form">
      {error && <p role="alert">{errorText(error)}</p>}
      <fieldset disabled={isLoading}>
        <label>
          X (целое, больше -954)
          <input
            type="number"
            step={1}
            min={-953}
            max={2147483647}
            required
            value={value.x}
            onChange={(event) => setValue({ ...value, x: event.target.value })}
          />
        </label>
        <Issues issues={issues} field="x" />
        <label>
          Y (целое, больше -392)
          <input
            type="number"
            step={1}
            min={-391}
            required
            value={value.y}
            onChange={(event) => setValue({ ...value, y: event.target.value })}
          />
        </label>
        <Issues issues={issues} field="y" />
        <button type="submit">{isLoading ? 'Сохранение…' : submitTitle}</button>
      </fieldset>
    </form>
  )
}

function CoordinatesPage() {
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [sortBy, setSortBy] = useState('id')
  const [sortOrder, setSortOrder] = useState('asc')
  const [editing, setEditing] = useState<Coordinates | null>(null)
  const [createdId, setCreatedId] = useState<number | null>(null)
  const [formKey, setFormKey] = useState(0)

  const list = useListCoordinatesQuery({ page, page_size: pageSize, sort_by: sortBy, sort_order: sortOrder })
  const [create, createState] = useCreateCoordinatesMutation()
  const [update, updateState] = useUpdateCoordinatesMutation()
  const [remove, removeState] = useDeleteCoordinatesMutation()

  async function handleCreate(body: { x: number; y: number }) {
    const result = await create(body)
    if (result.data) {
      setCreatedId(result.data.id)
      setFormKey(formKey + 1)
    }
  }

  async function handleUpdate(body: { x: number; y: number }) {
    if (editing && !(await update({ id: editing.id, body })).error) {
      setEditing(null)
    }
  }

  function startEdit(item: Coordinates) {
    updateState.reset()
    setEditing(item)
  }

  function handleDelete(item: Coordinates) {
    if (window.confirm(`Удалить координаты с ID ${item.id}? Это действие нельзя отменить.`)) {
      remove(item.id)
    }
  }

  return (
    <>
      <h2>Координаты</h2>

      <h3>Новые координаты</h3>
      {createdId !== null && <p>Координаты созданы, ID {createdId}.</p>}
      <CoordinatesForm
        key={formKey}
        initial={{ x: '', y: '' }}
        submitTitle="Создать"
        isLoading={createState.isLoading}
        error={createState.error}
        onSubmit={handleCreate}
      />

      <h3>Все координаты</h3>
      <SortControls
        fields={[
          ['id', 'ID'],
          ['x', 'X'],
          ['y', 'Y'],
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
      {list.data && list.data.items.length === 0 && <p>Координат нет.</p>}
      {list.data && list.data.items.length > 0 && (
        <div className="table-scroll">
          <table>
            <thead>
              <tr>
                <th></th>
                <th>ID</th>
                <th>X</th>
                <th>Y</th>
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
        <Modal title={`Изменение координат с ID ${editing.id}`} onClose={() => setEditing(null)}>
          <CoordinatesForm
            initial={{ x: String(editing.x), y: String(editing.y) }}
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

export default CoordinatesPage
