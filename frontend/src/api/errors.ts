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
  return messages[message] ?? statusMessages[error.status] ?? `Ошибка сервера (код ${error.status}).`
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
