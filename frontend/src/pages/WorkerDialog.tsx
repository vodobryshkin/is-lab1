import { useEffect, useRef } from 'react'
import type { ReactNode } from 'react'
import { useGetWorkerQuery } from '../api/api'
import type { Address, Worker } from '../api/api'
import { errorText } from '../api/errors'
import { formatDate, formatLocation, formatValue } from '../format'

interface Props {
  id: number
  onClose: () => void
  onEdit?: (worker: Worker) => void
}

function Fields({ rows }: { rows: [string, ReactNode][] }) {
  return (
    <table>
      <tbody>
        {rows.map(([title, value]) => (
          <tr key={title}>
            <th>{title}</th>
            <td>{value}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}

function addressText(address: Address) {
  const town = address.town ? `, город ${formatLocation(address.town)}, ID локации ${address.town.id}` : ''
  return `${address.street} (ID ${address.id})${town}`
}

function WorkerDialog({ id, onClose, onEdit }: Props) {
  const dialogRef = useRef<HTMLDialogElement>(null)
  const { data: worker, error, isFetching } = useGetWorkerQuery(id)

  useEffect(() => {
    dialogRef.current?.showModal()
  }, [])

  return (
    <dialog ref={dialogRef} onClose={onClose}>
      <h2>Работник с ID {id}</h2>
      {isFetching && <p>Загрузка…</p>}
      {error && (
        <p role="alert">
          {'status' in error && error.status === 404 ? 'Работник с таким ID не найден.' : errorText(error)}
        </p>
      )}

      {worker && !error && (
        <>
          <Fields
            rows={[
              ['ID', worker.id],
              ['Имя', worker.name],
              ['Дата создания', formatDate(worker.creation_date)],
              ['Зарплата', worker.salary],
              ['Рейтинг', worker.rating],
              ['Дата начала работы', formatDate(worker.start_date)],
              ['Должность', formatValue(worker.position)],
              ['Статус', formatValue(worker.status)],
              ['ID создателя', worker.creator_id],
            ]}
          />

          <h3>Координаты</h3>
          <Fields
            rows={[
              ['ID', worker.coordinates.id],
              ['X', worker.coordinates.x],
              ['Y', worker.coordinates.y],
            ]}
          />

          <h3>Организация</h3>
          {worker.organization ? (
            <Fields
              rows={[
                ['ID', worker.organization.id],
                ['Полное название', worker.organization.full_name],
                ['Годовой оборот', worker.organization.annual_turnover],
                ['Количество сотрудников', worker.organization.employees_count],
                ['Рейтинг', worker.organization.rating],
                ['Тип', formatValue(worker.organization.type)],
                ['Официальный адрес', addressText(worker.organization.official_address)],
                ['Почтовый адрес', addressText(worker.organization.postal_address)],
              ]}
            />
          ) : (
            <p>Не указана.</p>
          )}

          <h3>Персона</h3>
          {worker.person ? (
            <Fields
              rows={[
                ['ID', worker.person.id],
                ['Цвет глаз', formatValue(worker.person.eye_color)],
                ['Цвет волос', worker.person.hair_color],
                ['Местоположение', `${formatLocation(worker.person.location)}, ID локации ${worker.person.location.id}`],
                ['Дата рождения', new Date(worker.person.birthday).toLocaleDateString('ru-RU')],
                ['Вес', worker.person.weight],
                ['Национальность', worker.person.nationality],
              ]}
            />
          ) : (
            <p>Не указана.</p>
          )}
        </>
      )}

      <form method="dialog">
        {worker && !error && onEdit && (
          <button type="button" onClick={() => onEdit(worker)}>
            Изменить
          </button>
        )}
        <button type="submit">Закрыть</button>
      </form>
    </dialog>
  )
}

export default WorkerDialog
