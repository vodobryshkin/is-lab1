import type { SerializedError } from '@reduxjs/toolkit'
import type { FetchBaseQueryError } from '@reduxjs/toolkit/query/react'

export interface ValidationIssue {
  field: string
  code: string
  message: string
}

type RequestError = FetchBaseQueryError | SerializedError | undefined

// Сервер присылает сообщения на английском, поэтому известные тексты переводятся здесь,
// а для остальных показывается общее описание по HTTP-статусу.
const messages: Record<string, string> = {
  'Username or password is incorrect.': 'Неверный логин или пароль.',
  'A user with this username already exists.': 'Пользователь с таким логином уже существует.',
  'The request body contains invalid JSON or a value of the wrong type.':
    'Запрос содержит некорректные данные.',
  'The request body must be a JSON object.': 'Запрос содержит некорректные данные.',
  'Another organization with this full_name already exists.':
    'Организация с таким полным названием уже существует.',
}

const objectNames: Record<string, string> = {
  Worker: 'работник',
  Coordinates: 'координаты',
  Location: 'локация',
  Address: 'адрес',
  Organization: 'организация',
  Person: 'персона',
}

function translateMessage(message: string): string | undefined {
  const notFound = message.match(/^(\w+) with id=(\d+) (does|do) not exist\.$/)
  if (notFound) {
    return `Не найден объект «${objectNames[notFound[1]] ?? notFound[1]}» с ID ${notFound[2]}.`
  }
  const assigned = message.match(/^Person with id=(\d+) is already assigned to another worker\.$/)
  if (assigned) {
    return `Персона с ID ${assigned[1]} уже привязана к другому работнику.`
  }
  return messages[message]
}

const fieldNames: Record<string, string> = {
  $: 'Запрос',
  id: 'ID',
  name: 'Имя',
  salary: 'Зарплата',
  rating: 'Рейтинг',
  start_date: 'Дата начала работы',
  coordinates: 'Координаты',
  x: 'X',
  y: 'Y',
  z: 'Z',
  person: 'Персона',
  location: 'Местоположение',
  eye_color: 'Цвет глаз',
  hair_color: 'Цвет волос',
  birthday: 'Дата рождения',
  weight: 'Вес',
  nationality: 'Национальность',
  organization: 'Организация',
  organization_id: 'ID организации',
  full_name: 'Полное название',
  annual_turnover: 'Годовой оборот',
  employees_count: 'Количество сотрудников',
  type: 'Тип',
  official_address: 'Официальный адрес',
  official_address_id: 'ID официального адреса',
  postal_address: 'Почтовый адрес',
  postal_address_id: 'ID почтового адреса',
  street: 'Улица',
  town: 'Город',
}

export function fieldLabel(field: string): string {
  return field
    .split('.')
    .map((part) => fieldNames[part] ?? part)
    .join(' → ')
}

const statusMessages: Record<number, string> = {
  400: 'Некорректный запрос.',
  401: 'Требуется вход в систему.',
  403: 'Запрос отклонён проверкой CSRF. Повторите действие.',
  404: 'Объект не найден.',
  409: 'Операция конфликтует с текущими данными.',
  422: 'Проверьте введённые данные.',
}

const issueMessages: Record<string, string> = {
  MUST_NOT_BE_NULL: 'Поле обязательно для заполнения.',
  MUST_NOT_BE_EMPTY: 'Поле не может быть пустым.',
  OUT_OF_RANGE: 'Значение вне допустимого диапазона.',
  MUST_MATCH_PATTERN: 'Значение имеет недопустимый формат.',
  MUST_BE_POSITIVE: 'Значение должно быть больше нуля.',
  INVALID_COMBINATION: 'Недопустимое сочетание полей.',
  MUST_BE_GREATER_THAN_MINUS_954: 'Значение должно быть больше -954.',
  MUST_BE_GREATER_THAN_MINUS_392: 'Значение должно быть больше -392.',
}

export function errorText(error: RequestError): string {
  if (!error) {
    return ''
  }
  if (!('status' in error)) {
    return 'Неизвестная ошибка.'
  }
  if (error.status === 'FETCH_ERROR') {
    return 'Сервер недоступен. Проверьте, что бэкенд запущен.'
  }
  if (typeof error.status !== 'number') {
    return 'Некорректный ответ сервера.'
  }
  if (error.status === 401 || error.status === 403 || error.status === 422) {
    return statusMessages[error.status]
  }

  const message = (error.data as { message?: string } | null)?.message ?? ''
  return translateMessage(message) ?? statusMessages[error.status] ?? `Ошибка сервера (код ${error.status}).`
}

export function validationIssues(error: RequestError): ValidationIssue[] {
  if (error && 'status' in error && error.status === 422) {
    const issues = (error.data as { errors?: ValidationIssue[] } | null)?.errors ?? []
    return issues.map((issue) => ({
      ...issue,
      message: issueMessages[issue.code] ?? 'Недопустимое значение.',
    }))
  }
  return []
}

export function isUnauthorized(error: RequestError): boolean {
  return !!error && 'status' in error && error.status === 401
}
