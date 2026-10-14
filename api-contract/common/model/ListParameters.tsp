import "@typespec/http";

using TypeSpec.Http;

namespace WorkersApi.Domain;

@doc("Подстрока без учёта регистра. Пустая строка игнорируется; % и _ считаются обычными символами.")
scalar SubstringFilter extends string;

@doc("Параметры страницы и порядка сортировки.")
model ListParameters {
    @doc("Номер страницы с 1. За пределами списка возвращается пустая страница.")
    @query
    @minValue(1)
    page?: int32 = 1;

    @doc("Количество объектов на странице.")
    @query
    @minValue(1)
    @maxValue(100)
    page_size?: int32 = 20;

    @doc("Направление сортировки. null всегда последними; при равенстве значений — ID по возрастанию.")
    @query
    sort_order?: "asc" | "desc" = "asc";
}
