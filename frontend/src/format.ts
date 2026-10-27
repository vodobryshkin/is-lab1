import type { Location } from './api/api'

export function formatDate(value: string): string {
  return new Date(value).toLocaleString('ru-RU')
}

export function formatLocation(location: Location): string {
  return `(${location.x}; ${location.y}; ${location.z})`
}

export function formatValue(value: string | number | null): string {
  return value === null ? '—' : String(value)
}
