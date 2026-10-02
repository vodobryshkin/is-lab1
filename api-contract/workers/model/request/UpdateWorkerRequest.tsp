import "../../../common/model/WorkerFields.tsp";

namespace WorkersApi.Workers;

using WorkersApi.Domain;

@doc("Данные, которые клиент отправляет при обновлении работника.")
@example(#{
    ...workerExample,
    organization_id: 1
})
@withOptionalProperties
model UpdateWorkerRequest {
    ...WorkerFields;

    @doc("Новый Id организации, к которой принадлежит работник.")
    @minValueExclusive(0)
    organization_id: int32;
}