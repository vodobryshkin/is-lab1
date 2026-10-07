namespace WorkersApi.Organizations;

const indexSalariesExample: IndexSalariesRequest = #{
    coefficient: 1.1
};

@doc("Данные для индексации зарплат.")
@example(indexSalariesExample)
model IndexSalariesRequest {
    ...Record<never>;

    @doc("Множитель зарплаты. Например, 1.1 увеличивает её на 10%.")
    @minValueExclusive(0)
    coefficient: float64;
}