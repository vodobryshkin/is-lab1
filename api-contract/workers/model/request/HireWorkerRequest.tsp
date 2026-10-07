namespace WorkersApi.Workers;

const hireWorkerExample: HireWorkerRequest = #{
    organization_id: 1
};

@doc("Данные для приёма работника.")
@example(hireWorkerExample)
model HireWorkerRequest {
    ...Record<never>;

    @doc("ID существующей организации.")
    @minValue(1)
    organization_id: int32;
}