import { useState } from 'react'
import type { FormEvent } from 'react'
import { useDeleteWorkerMutation, useListWorkersQuery } from '../api/api'
import type { Worker } from '../api/api'
import { errorText } from '../api/errors'
import Pager from '../components/Pager'
import { formatDate, formatValue } from '../format'
import EditWorkerDialog from './EditWorkerDialog'
import WorkerDialog from './WorkerDialog'

const sortFields = [
  ['id', 'ID'],
  ['name', 'Имя'],
  ['salary', 'Зарплата'],
  ['rating', 'Рейтинг'],
  ['start_date', 'Дата начала работы'],
  ['creation_date', 'Дата создания'],
  ['position', 'Должность'],
  ['status', 'Статус'],
]

const emptyFilters = { name: '', position: '', status: '' }

function WorkersPage() {
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [sortBy, setSortBy] = useState('id')
  const [sortOrder, setSortOrder] = useState('asc')
  const [filterInputs, setFilterInputs] = useState(emptyFilters)
  const [filters, setFilters] = useState(emptyFilters)
  const [idInput, setIdInput] = useState('')
  const [openedId, setOpenedId] = useState<number | null>(null)
  const [editing, setEditing] = useState<Worker | null>(null)
  const [deleteWorker, deleteState] = useDeleteWorkerMutation()

  const list = useListWorkersQuery({
    page,
    page_size: pageSize,
    sort_by: sortBy,
    sort_order: sortOrder,
    name: filters.name || undefined,
    position: filters.position || undefined,
    status: filters.status || undefined,
  })

  async function handleDelete(worker: Worker) {
    if (!window.confirm(`Удалить работника «${worker.name}» (ID ${worker.id})? Это действие нельзя отменить.`)) {
      return
    }
    const result = await deleteWorker(worker.id)
    if (!result.error) {
      setOpenedId(null)
    }
  }

  function applyFilters(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setFilters({
      name: filterInputs.name.trim(),
      position: filterInputs.position.trim(),
      status: filterInputs.status.trim(),
    })
    setPage(1)
  }

  function resetFilters() {
    setFilterInputs(emptyFilters)
    setFilters(emptyFilters)
    setPage(1)
  }

  function openDetails(id: number) {
    deleteState.reset()
    setOpenedId(id)
  }

  function openById(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    openDetails(Number(idInput))
  }

  return (
    <>
      <h2>Список работников</h2>

      <form onSubmit={openById} className="inline-form">
        <label>
          ID работника
          <input
            type="number"
            min={1}
            max={2147483647}
            step={1}
            required
            value={idInput}
            onChange={(event) => setIdInput(event.target.value)}
          />
        </label>
        <button type="submit">Открыть</button>
      </form>

      <form onSubmit={applyFilters} className="inline-form">
        <label>
          Имя содержит
          <input
            type="text"
            value={filterInputs.name}
            onChange={(event) => setFilterInputs({ ...filterInputs, name: event.target.value })}
          />
        </label>
        <label>
          Должность содержит
          <input
            type="text"
            placeholder="например, DEV"
            value={filterInputs.position}
            onChange={(event) => setFilterInputs({ ...filterInputs, position: event.target.value })}
          />
        </label>
        <label>
          Статус содержит
          <input
            type="text"
            placeholder="например, HIR"
            value={filterInputs.status}
            onChange={(event) => setFilterInputs({ ...filterInputs, status: event.target.value })}
          />
        </label>
        <button type="submit">Найти</button>
        <button type="button" onClick={resetFilters}>
          Сбросить
        </button>
      </form>

      <div className="inline-form">
        <label>
          Сортировать по
          <select
            value={sortBy}
            onChange={(event) => {
              setSortBy(event.target.value)
              setPage(1)
            }}
          >
            {sortFields.map(([value, title]) => (
              <option key={value} value={value}>
                {title}
              </option>
            ))}
          </select>
        </label>
        <label>
          Порядок
          <select
            value={sortOrder}
            onChange={(event) => {
              setSortOrder(event.target.value)
              setPage(1)
            }}
          >
            <option value="asc">по возрастанию</option>
            <option value="desc">по убыванию</option>
          </select>
        </label>
      </div>

      {deleteState.isError && <p role="alert">{errorText(deleteState.error)}</p>}
      {list.isLoading && <p>Загрузка…</p>}
      {list.isError && <p role="alert">{errorText(list.error)}</p>}
      {list.data && list.data.items.length === 0 && <p>Работники не найдены.</p>}

      {list.data && list.data.items.length > 0 && (
        <div className="table-scroll">
          <table>
            <thead>
              <tr>
                <th></th>
                <th>ID</th>
                <th>Имя</th>
                <th>Координаты</th>
                <th>Дата создания</th>
                <th>Организация</th>
                <th>Зарплата</th>
                <th>Рейтинг</th>
                <th>Дата начала работы</th>
                <th>Должность</th>
                <th>Статус</th>
                <th>Персона</th>
              </tr>
            </thead>
            <tbody>
              {list.data.items.map((worker) => (
                <tr key={worker.id}>
                  <td>
                    <div className="row-actions">
                      <button type="button" onClick={() => openDetails(worker.id)}>
                        Подробнее
                      </button>
                      <button type="button" onClick={() => setEditing(worker)}>
                        Изменить
                      </button>
                      <button type="button" onClick={() => handleDelete(worker)} disabled={deleteState.isLoading}>
                        Удалить
                      </button>
                    </div>
                  </td>
                  <td>{worker.id}</td>
                  <td>{worker.name}</td>
                  <td>
                    ({worker.coordinates.x}; {worker.coordinates.y})
                  </td>
                  <td>{formatDate(worker.creation_date)}</td>
                  <td>{worker.organization ? worker.organization.full_name : '—'}</td>
                  <td>{worker.salary}</td>
                  <td>{worker.rating}</td>
                  <td>{formatDate(worker.start_date)}</td>
                  <td>{formatValue(worker.position)}</td>
                  <td>{formatValue(worker.status)}</td>
                  <td>{worker.person ? `ID ${worker.person.id}` : '—'}</td>
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

      {openedId !== null && (
        <WorkerDialog
          id={openedId}
          onClose={() => setOpenedId(null)}
          onEdit={(worker) => {
            setOpenedId(null)
            setEditing(worker)
          }}
          onDelete={handleDelete}
          deleteError={deleteState.error}
        />
      )}
      {editing && <EditWorkerDialog key={editing.id} worker={editing} onClose={() => setEditing(null)} />}
    </>
  )
}

export default WorkersPage
