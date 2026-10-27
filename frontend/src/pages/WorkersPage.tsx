import { useState } from 'react'
import type { FormEvent } from 'react'
import { useListWorkersQuery } from '../api/api'
import { errorText } from '../api/errors'
import { formatDate, formatValue } from '../format'
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

  const list = useListWorkersQuery({
    page,
    page_size: pageSize,
    sort_by: sortBy,
    sort_order: sortOrder,
    name: filters.name || undefined,
    position: filters.position || undefined,
    status: filters.status || undefined,
  })

  const totalPages = list.data ? Math.max(1, Math.ceil(list.data.total_items / pageSize)) : 1

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

  function openById(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setOpenedId(Number(idInput))
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
        <label>
          На странице
          <select
            value={pageSize}
            onChange={(event) => {
              setPageSize(Number(event.target.value))
              setPage(1)
            }}
          >
            {[5, 10, 20, 50, 100].map((size) => (
              <option key={size} value={size}>
                {size}
              </option>
            ))}
          </select>
        </label>
      </div>

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
                    <button type="button" onClick={() => setOpenedId(worker.id)}>
                      Подробнее
                    </button>
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
        <div className="inline-form">
          <button type="button" onClick={() => setPage(page - 1)} disabled={page <= 1}>
            Назад
          </button>
          <span>
            Страница {page} из {totalPages}, всего работников: {list.data.total_items}
            {list.isFetching && ' (обновление…)'}
          </span>
          <button type="button" onClick={() => setPage(page + 1)} disabled={page >= totalPages}>
            Вперёд
          </button>
        </div>
      )}

      {openedId !== null && <WorkerDialog id={openedId} onClose={() => setOpenedId(null)} />}
    </>
  )
}

export default WorkersPage
