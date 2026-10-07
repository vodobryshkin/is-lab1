namespace WorkersApi.Ok;

const idResponseExample: IdResponse = #{
    id: 42
};

@doc("ID созданного объекта.")
@example(idResponseExample)
model IdResponse {
    @doc("ID объекта.")
    @minValueExclusive(0)
    id: int32;
}