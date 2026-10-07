namespace WorkersApi.Ok;

const pageResponseExample: PageResponse<unknown> = #{
    items: #[],
    page: 1,
    page_size: 20,
    total_items: 0
};

@doc("Страница объектов.")
@example(pageResponseExample)
model PageResponse<TItem> {
    @doc("Объекты страницы.")
    items: TItem[];

    @doc("Номер страницы, начиная с 1.")
    @minValueExclusive(0)
    page: int32;

    @doc("Размер страницы.")
    @minValueExclusive(0)
    page_size: int32;

    @doc("Количество объектов с учётом фильтров.")
    @minValue(0)
    total_items: int64;
}