import "../../../common/model/WorkerFields.tsp";

namespace WorkersApi.Workers;

using WorkersApi.Domain;

@doc("Тело запроса на получение сотрудника в успешном случае.")
@example(#{
    ...workerExample,
    organization: organizationExample
})
model GetSingleWorkerResponse {
    ...WorkerFields;

    @doc("Организация, к которой принадлежит сотрудник.")
    organization: OrganizationFields | null;
}