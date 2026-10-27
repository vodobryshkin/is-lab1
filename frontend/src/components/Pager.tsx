interface Props {
  page: number
  pageSize: number
  totalItems: number
  isFetching: boolean
  onPage: (page: number) => void
  onPageSize: (pageSize: number) => void
}

function Pager({ page, pageSize, totalItems, isFetching, onPage, onPageSize }: Props) {
  const totalPages = Math.max(1, Math.ceil(totalItems / pageSize))

  return (
    <div className="inline-form">
      <button type="button" onClick={() => onPage(page - 1)} disabled={page <= 1}>
        Назад
      </button>
      <span>
        Страница {page} из {totalPages}, всего: {totalItems}
        {isFetching && ' (обновление…)'}
      </span>
      <button type="button" onClick={() => onPage(page + 1)} disabled={page >= totalPages}>
        Вперёд
      </button>
      <label>
        На странице
        <select value={pageSize} onChange={(event) => onPageSize(Number(event.target.value))}>
          {[5, 10, 20, 50, 100].map((size) => (
            <option key={size} value={size}>
              {size}
            </option>
          ))}
        </select>
      </label>
    </div>
  )
}

export default Pager
