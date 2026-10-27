import { useState } from 'react'
import type { FormEvent } from 'react'
import type { SerializedError } from '@reduxjs/toolkit'
import type { FetchBaseQueryError } from '@reduxjs/toolkit/query/react'
import {
  useCreateAddressMutation,
  useDeleteAddressMutation,
  useListAddressesQuery,
  useUpdateAddressMutation,
} from '../api/api'
import type { Address, AddressRequest } from '../api/api'
import { errorText, validationIssues } from '../api/errors'
import Issues from '../components/Issues'
import LocationInput from '../components/LocationInput'
import Modal from '../components/Modal'
import Pager from '../components/Pager'
import SortControls from '../components/SortControls'
import { addressRequest, addressStateFrom, emptyAddress } from '../forms'
import type { AddressState } from '../forms'
import { formatLocation } from '../format'

interface FormProps {
  initial: AddressState
  submitTitle: string
  isLoading: boolean
  error: FetchBaseQueryError | SerializedError | undefined
  onSubmit: (body: AddressRequest) => void
}

function AddressForm({ initial, submitTitle, isLoading, error, onSubmit }: FormProps) {
  const [value, setValue] = useState(initial)
  const issues = validationIssues(error)

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    onSubmit(addressRequest(value))
  }

  return (
    <form onSubmit={handleSubmit} className="worker-form">
      {error && <p role="alert">{errorText(error)}</p>}
      <fieldset disabled={isLoading}>
        <label>
          Улица
          <input
            type="text"
            required
            pattern=".*\S.*"
            title="Улица не может быть пустой"
            value={value.street}
            onChange={(event) => setValue({ ...value, street: event.target.value })}
          />
        </label>
        <Issues issues={issues} field="street" />
        <LocationInput
          legend="Город"
          value={value.town}
          onChange={(town) => setValue({ ...value, town })}
          allowNone={true}
          issues={issues}
          field="town"
        />
        <button type="submit">{isLoading ? 'Сохранение…' : submitTitle}</button>
      </fieldset>
    </form>
  )
}

function AddressesPage() {
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [sortBy, setSortBy] = useState('id')
  const [sortOrder, setSortOrder] = useState('asc')
  const [streetInput, setStreetInput] = useState('')
  const [street, setStreet] = useState('')
  const [editing, setEditing] = useState<Address | null>(null)
  const [createdId, setCreatedId] = useState<number | null>(null)
  const [formKey, setFormKey] = useState(0)

  const list = useListAddressesQuery({
    page,
    page_size: pageSize,
    sort_by: sortBy,
    sort_order: sortOrder,
    street: street || undefined,
  })
  const [create, createState] = useCreateAddressMutation()
  const [update, updateState] = useUpdateAddressMutation()
  const [remove, removeState] = useDeleteAddressMutation()

  async function handleCreate(body: AddressRequest) {
    const result = await create(body)
    if (result.data) {
      setCreatedId(result.data.id)
      setFormKey(formKey + 1)
    }
  }

  async function handleUpdate(body: AddressRequest) {
    if (editing && !(await update({ id: editing.id, body })).error) {
      setEditing(null)
    }
  }

  function startEdit(item: Address) {
    updateState.reset()
    setEditing(item)
  }

  function handleDelete(item: Address) {
    if (window.confirm(`Удалить адрес «${item.street}» (ID ${item.id})? Это действие нельзя отменить.`)) {
      remove(item.id)
    }
  }

  function applyFilter(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setStreet(streetInput.trim())
    setPage(1)
  }

  return (
    <>
      <h2>Адреса</h2>

      <h3>Новый адрес</h3>
      {createdId !== null && <p>Адрес создан, ID {createdId}.</p>}
      <AddressForm
        key={formKey}
        initial={emptyAddress}
        submitTitle="Создать"
        isLoading={createState.isLoading}
        error={createState.error}
        onSubmit={handleCreate}
      />

      <h3>Все адреса</h3>
      <form onSubmit={applyFilter} className="inline-form">
        <label>
          Улица содержит
          <input type="text" value={streetInput} onChange={(event) => setStreetInput(event.target.value)} />
        </label>
        <button type="submit">Найти</button>
        <button
          type="button"
          onClick={() => {
            setStreetInput('')
            setStreet('')
            setPage(1)
          }}
        >
          Сбросить
        </button>
      </form>
      <SortControls
        fields={[
          ['id', 'ID'],
          ['street', 'Улица'],
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
      {list.data && list.data.items.length === 0 && <p>Адреса не найдены.</p>}
      {list.data && list.data.items.length > 0 && (
        <div className="table-scroll">
          <table>
            <thead>
              <tr>
                <th></th>
                <th>ID</th>
                <th>Улица</th>
                <th>Город (локация)</th>
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
                  <td>{item.street}</td>
                  <td>{item.town ? `${formatLocation(item.town)}, ID ${item.town.id}` : '—'}</td>
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
        <Modal title={`Изменение адреса с ID ${editing.id}`} onClose={() => setEditing(null)}>
          <AddressForm
            initial={addressStateFrom(editing)}
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

export default AddressesPage
