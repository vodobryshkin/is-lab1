import { useState } from 'react'
import type { FormEvent } from 'react'
import {
  choiceParams,
  statuses,
  useHireWorkerMutation,
  useIndexSalariesMutation,
  useLazyCountByStatusGreaterThanQuery,
  useLazyCountByStatusQuery,
  useLazyRatingLessThanQuery,
  useListOrganizationsQuery,
} from '../api/api'
import { errorText, validationIssues } from '../api/errors'
import IdInput from '../components/IdInput'
import Issues from '../components/Issues'
import { formatDate, formatValue } from '../format'

function SpecialPage() {
  const [equalStatus, setEqualStatus] = useState('HIRED')
  const [greaterStatus, setGreaterStatus] = useState('HIRED')
  const [rating, setRating] = useState('')
  const [hireWorkerId, setHireWorkerId] = useState('')
  const [hireOrganizationId, setHireOrganizationId] = useState('')
  const [indexOrganizationId, setIndexOrganizationId] = useState('')
  const [coefficient, setCoefficient] = useState('')

  const [countEqual, countEqualState] = useLazyCountByStatusQuery()
  const [countGreater, countGreaterState] = useLazyCountByStatusGreaterThanQuery()
  const [findByRating, ratingState] = useLazyRatingLessThanQuery()
  const [hire, hireState] = useHireWorkerMutation()
  const [indexSalaries, indexState] = useIndexSalariesMutation()
  const organizations = useListOrganizationsQuery(choiceParams)
  const organizationChoices = (organizations.data?.items ?? []).map((item) => ({ id: item.id, title: item.full_name }))

  function handleHire(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    hire({ id: Number(hireWorkerId), organization_id: Number(hireOrganizationId) })
  }

  function handleIndex(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    indexSalaries({ id: Number(indexOrganizationId), coefficient: Number(coefficient) })
  }

  return (
    <>
      <h2>Специальные операции</h2>

      <h3>Количество работников с заданным статусом</h3>
      <form
        className="inline-form"
        onSubmit={(event) => {
          event.preventDefault()
          countEqual(equalStatus)
        }}
      >
        <label>
          Статус
          <select value={equalStatus} onChange={(event) => setEqualStatus(event.target.value)}>
            {statuses.map((status) => (
              <option key={status}>{status}</option>
            ))}
            <option value="null">не указан (null)</option>
          </select>
        </label>
        <button type="submit">Посчитать</button>
      </form>
      {countEqualState.isFetching && <p>Загрузка…</p>}
      {countEqualState.isError && <p role="alert">{errorText(countEqualState.error)}</p>}
      {countEqualState.data && !countEqualState.isError && (
        <p>
          Работников со статусом {countEqualState.originalArgs === 'null' ? 'не указан' : countEqualState.originalArgs}:{' '}
          <strong>{countEqualState.data.count}</strong>
        </p>
      )}

      <h3>Количество работников со статусом больше заданного</h3>
      <p>Порядок статусов: HIRED &lt; RECOMMENDED_FOR_PROMOTION &lt; REGULAR. Работники без статуса не учитываются.</p>
      <form
        className="inline-form"
        onSubmit={(event) => {
          event.preventDefault()
          countGreater(greaterStatus)
        }}
      >
        <label>
          Статус
          <select value={greaterStatus} onChange={(event) => setGreaterStatus(event.target.value)}>
            {statuses.map((status) => (
              <option key={status}>{status}</option>
            ))}
          </select>
        </label>
        <button type="submit">Посчитать</button>
      </form>
      {countGreaterState.isFetching && <p>Загрузка…</p>}
      {countGreaterState.isError && <p role="alert">{errorText(countGreaterState.error)}</p>}
      {countGreaterState.data && !countGreaterState.isError && (
        <p>
          Работников со статусом больше {countGreaterState.originalArgs}: <strong>{countGreaterState.data.count}</strong>
        </p>
      )}

      <h3>Работники с рейтингом меньше заданного</h3>
      <form
        className="inline-form"
        onSubmit={(event) => {
          event.preventDefault()
          findByRating(Number(rating))
        }}
      >
        <label>
          Рейтинг меньше
          <input
            type="number"
            step={1}
            min={-2147483648}
            max={2147483647}
            required
            value={rating}
            onChange={(event) => setRating(event.target.value)}
          />
        </label>
        <button type="submit">Найти</button>
      </form>
      {ratingState.isFetching && <p>Загрузка…</p>}
      {ratingState.isError && <p role="alert">{errorText(ratingState.error)}</p>}
      {ratingState.data && !ratingState.isError && ratingState.data.length === 0 && (
        <p>Работников с рейтингом меньше {ratingState.originalArgs} нет.</p>
      )}
      {ratingState.data && !ratingState.isError && ratingState.data.length > 0 && (
        <>
          <p>
            Найдено работников: {ratingState.data.length} (рейтинг меньше {ratingState.originalArgs}).
          </p>
          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Имя</th>
                  <th>Рейтинг</th>
                  <th>Зарплата</th>
                  <th>Статус</th>
                  <th>Должность</th>
                  <th>Организация</th>
                  <th>Дата начала работы</th>
                </tr>
              </thead>
              <tbody>
                {ratingState.data.map((worker) => (
                  <tr key={worker.id}>
                    <td>{worker.id}</td>
                    <td>{worker.name}</td>
                    <td>{worker.rating}</td>
                    <td>{worker.salary}</td>
                    <td>{formatValue(worker.status)}</td>
                    <td>{formatValue(worker.position)}</td>
                    <td>{worker.organization ? worker.organization.full_name : '—'}</td>
                    <td>{formatDate(worker.start_date)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}

      <h3>Принять работника в организацию</h3>
      <p>Работник получает выбранную организацию и статус HIRED. Прежняя организация заменяется.</p>
      <form onSubmit={handleHire} className="worker-form">
        <fieldset disabled={hireState.isLoading}>
          <label>
            ID работника
            <input
              type="number"
              step={1}
              min={1}
              max={2147483647}
              required
              value={hireWorkerId}
              onChange={(event) => {
                setHireWorkerId(event.target.value)
                hireState.reset()
              }}
            />
          </label>
          <IdInput
            label="ID организации"
            value={hireOrganizationId}
            onChange={(id) => {
              setHireOrganizationId(id)
              hireState.reset()
            }}
            choices={organizationChoices}
            issues={validationIssues(hireState.error)}
            field="organization_id"
          />
          <button type="submit">{hireState.isLoading ? 'Выполнение…' : 'Принять'}</button>
        </fieldset>
      </form>
      {hireState.isError && <p role="alert">{errorText(hireState.error)}</p>}
      {hireState.isSuccess && (
        <p>
          Работник с ID {hireState.originalArgs?.id} принят в организацию с ID {hireState.originalArgs?.organization_id}.
        </p>
      )}

      <h3>Проиндексировать зарплаты сотрудников организации</h3>
      <p>Зарплата каждого сотрудника умножается на коэффициент, например 1.1 — увеличение на 10%.</p>
      <form onSubmit={handleIndex} className="worker-form">
        <fieldset disabled={indexState.isLoading}>
          <IdInput
            label="ID организации"
            value={indexOrganizationId}
            onChange={(id) => {
              setIndexOrganizationId(id)
              indexState.reset()
            }}
            choices={organizationChoices}
            issues={[]}
            field=""
          />
          <label>
            Коэффициент (больше 0)
            <input
              type="number"
              step="any"
              min={0}
              required
              value={coefficient}
              onChange={(event) => {
                setCoefficient(event.target.value)
                indexState.reset()
              }}
            />
          </label>
          <Issues issues={validationIssues(indexState.error)} field="coefficient" />
          <button type="submit">{indexState.isLoading ? 'Выполнение…' : 'Проиндексировать'}</button>
        </fieldset>
      </form>
      {indexState.isError && <p role="alert">{errorText(indexState.error)}</p>}
      {indexState.data && (
        <p>
          Зарплаты проиндексированы на коэффициент {indexState.originalArgs?.coefficient}. Изменено работников:{' '}
          <strong>{indexState.data.count}</strong>.
        </p>
      )}
    </>
  )
}

export default SpecialPage
