namespace WorkersApi.Ok;

const countResponseExample: CountResponse = #{
    count: 12
};

@doc("Количество объектов.")
@example(countResponseExample)
model CountResponse {
    @doc("Количество объектов.")
    @minValue(0)
    count: int64;
}