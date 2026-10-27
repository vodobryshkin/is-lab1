import { useState } from 'react'
import type { FormEvent } from 'react'
import type { SerializedError } from '@reduxjs/toolkit'
import type { FetchBaseQueryError } from '@reduxjs/toolkit/query/react'
import {
  useCreateOrganizationMutation,
  useDeleteOrganizationMutation,
  useListOrganizationsQuery,
  useUpdateOrganizationMutation,
} from '../api/api'
import type { Address, Organization, OrganizationRequest } from '../api/api'
import { errorText, fieldLabel, validationIssues } from '../api/errors'
import Modal from '../components/Modal'
import OrganizationFields from '../components/OrganizationFields'
import Pager from '../components/Pager'
import SortControls from '../components/SortControls'
import { emptyOrganization, organizationRequest, organizationStateFrom } from '../forms'
import type { OrganizationState } from '../forms'
import { formatLocation, formatValue } from '../format'

interface FormProps {
  initial: OrganizationState
  submitTitle: string
  isLoading: boolean
  error: FetchBaseQueryError | SerializedError | undefined
  onSubmit: (body: OrganizationRequest) => void
}

function OrganizationForm({ initial, submitTitle, isLoading, error, onSubmit }: FormProps) {
  const [value, setValue] = useState(initial)
  const issues = validationIssues(error)

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    onSubmit(organizationRequest(value))
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
        <OrganizationFields value={value} onChange={setValue} issues={issues} field="" />
        <button type="submit">{isLoading ? 'Сохранение…' : submitTitle}</button>
      </fieldset>
    </form>
  )
}

function addressText(address: Address) {
  const town = address.town ? `, город ${formatLocation(address.town)}` : ''
  return `${address.street} (ID ${address.id})${town}`
}

const emptyFilters = { full_name: '', type: '' }

function OrganizationsPage() {
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [sortBy, setSortBy] = useState('id')
  const [sortOrder, setSortOrder] = useState('asc')
  const [filterInputs, setFilterInputs] = useState(emptyFilters)
  const [filters, setFilters] = useState(emptyFilters)
  const [editing, setEditing] = useState<Organization | null>(null)
  const [createdId, setCreatedId] = useState<number | null>(null)
  const [formKey, setFormKey] = useState(0)

  const list = useListOrganizationsQuery({
    page,
    page_size: pageSize,
    sort_by: sortBy,
    sort_order: sortOrder,
    full_name: filters.full_name || undefined,
    type: filters.type || undefined,
  })
  const [create, createState] = useCreateOrganizationMutation()
  const [update, updateState] = useUpdateOrganizationMutation()
  const [remove, removeState] = useDeleteOrganizationMutation()

  async function handleCreate(body: OrganizationRequest) {
    const result = await create(body)
    if (result.data) {
      setCreatedId(result.data.id)
      setFormKey(formKey + 1)
    }
  }

  async function handleUpdate(body: OrganizationRequest) {
    if (editing && !(await update({ id: editing.id, body })).error) {
      setEditing(null)
    }
  }

  function startEdit(item: Organization) {
    updateState.reset()
    setEditing(item)
  }

  function handleDelete(item: Organization) {
    if (window.confirm(`Удалить организацию «${item.full_name}» (ID ${item.id})? Это действие нельзя отменить.`)) {
      remove(item.id)
    }
  }

  function applyFilters(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setFilters({ full_name: filterInputs.full_name.trim(), type: filterInputs.type.trim() })
    setPage(1)
  }

  return (
    <>
      <h2>Организации</h2>

      <h3>Новая организация</h3>
      {createdId !== null && <p>Организация создана, ID {createdId}.</p>}
      <OrganizationForm
        key={formKey}
        initial={emptyOrganization}
        submitTitle="Создать"
        isLoading={createState.isLoading}
        error={createState.error}
        onSubmit={handleCreate}
      />

      <h3>Все организации</h3>
      <form onSubmit={applyFilters} className="inline-form">
        <label>
          Название содержит
          <input
            type="text"
            value={filterInputs.full_name}
            onChange={(event) => setFilterInputs({ ...filterInputs, full_name: event.target.value })}
          />
        </label>
        <label>
          Тип содержит
          <input
            type="text"
            placeholder="например, TRUST"
            value={filterInputs.type}
            onChange={(event) => setFilterInputs({ ...filterInputs, type: event.target.value })}
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
          ['full_name', 'Полное название'],
          ['annual_turnover', 'Годовой оборот'],
          ['employees_count', 'Количество сотрудников'],
          ['rating', 'Рейтинг'],
          ['type', 'Тип'],
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
      {list.data && list.data.items.length === 0 && <p>Организации не найдены.</p>}
      {list.data && list.data.items.length > 0 && (
        <div className="table-scroll">
          <table>
            <thead>
              <tr>
                <th></th>
                <th>ID</th>
                <th>Полное название</th>
                <th>Годовой оборот</th>
                <th>Сотрудников</th>
                <th>Рейтинг</th>
                <th>Тип</th>
                <th>Официальный адрес</th>
                <th>Почтовый адрес</th>
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
                  <td>{item.full_name}</td>
                  <td>{item.annual_turnover}</td>
                  <td>{item.employees_count}</td>
                  <td>{item.rating}</td>
                  <td>{formatValue(item.type)}</td>
                  <td>{addressText(item.official_address)}</td>
                  <td>{addressText(item.postal_address)}</td>
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
        <Modal title={`Изменение организации с ID ${editing.id}`} onClose={() => setEditing(null)}>
          <OrganizationForm
            initial={organizationStateFrom(editing)}
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

export default OrganizationsPage
