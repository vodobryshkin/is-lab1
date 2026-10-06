import "../../../common/ok/CreatedResponse.tsp";

namespace WorkersApi.Workers;

const createdWorkerExample: CreateWorkerResponse = #{
    id: 42,
    organization_id: 1
};

const createWorkerCreatedExample:
    WorkersApi.Ok.CreatedResponse<CreateWorkerResponse> = #{
        statusCode: 201,
        contentType: "application/json",
        location: "/api/v1/workers/42",
        body: createdWorkerExample
    };

@doc("Результат создания работника.")
@example(createdWorkerExample)
model CreateWorkerResponse {
    @doc("ID работника.")
    @minValueExclusive(0)
    id: int32;

    @doc("ID организации или null.")
    @minValueExclusive(0)
    organization_id: int32 | null;
}