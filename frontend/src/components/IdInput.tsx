import { useId } from 'react'
import type { ValidationIssue } from '../api/errors'
import Issues from './Issues'

interface Props {
  label: string
  value: string
  onChange: (value: string) => void
  choices: { id: number; title: string }[]
  issues: ValidationIssue[]
  field: string
}

// Поле ID существующего объекта с подсказками из списка (браузер показывает их при вводе).
function IdInput({ label, value, onChange, choices, issues, field }: Props) {
  const listId = useId()
  return (
    <>
      <label>
        {label}
        <input
          type="number"
          min={1}
          step={1}
          required
          list={listId}
          value={value}
          onChange={(event) => onChange(event.target.value)}
        />
      </label>
      <datalist id={listId}>
        {choices.map((choice) => (
          <option key={choice.id} value={choice.id}>
            {choice.title}
          </option>
        ))}
      </datalist>
      <small>Введите ID или выберите из подсказок.</small>
      <Issues issues={issues} field={field} />
    </>
  )
}

export default IdInput
