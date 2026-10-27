interface Props {
  fields: [string, string][]
  sortBy: string
  sortOrder: string
  onChange: (sortBy: string, sortOrder: string) => void
}

function SortControls({ fields, sortBy, sortOrder, onChange }: Props) {
  return (
    <div className="inline-form">
      <label>
        Сортировать по
        <select value={sortBy} onChange={(event) => onChange(event.target.value, sortOrder)}>
          {fields.map(([value, title]) => (
            <option key={value} value={value}>
              {title}
            </option>
          ))}
        </select>
      </label>
      <label>
        Порядок
        <select value={sortOrder} onChange={(event) => onChange(sortBy, event.target.value)}>
          <option value="asc">по возрастанию</option>
          <option value="desc">по убыванию</option>
        </select>
      </label>
    </div>
  )
}

export default SortControls
