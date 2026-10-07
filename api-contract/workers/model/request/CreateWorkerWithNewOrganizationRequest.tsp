import "../../../common/model/WorkerFields.tsp";
import "../../../common/model/OrganizationRequest.tsp";

using WorkersApi.Domain;

namespace WorkersApi.Workers;

const createWorkerWithNewOrganizationExample:
    CreateWorkerWithNewOrganizationRequest = #{
        ...workerExample,
        organization: organizationWithNewAddressesExample
    };

const createWorkerWithExistingAddressesExample = #{
    ...createWorkerWithNewOrganizationExample,
    organization: organizationWithExistingAddressesExample
};

const createWorkerWithNewOfficialAddressExample = #{
    ...createWorkerWithNewOrganizationExample,
    organization: organizationWithNewOfficialAddressExample
};

const createWorkerWithNewPostalAddressExample = #{
    ...createWorkerWithNewOrganizationExample,
    organization: organizationWithNewPostalAddressExample
};

@doc("Создание работника вместе с новой организацией.")
@example(createWorkerWithNewOrganizationExample)
@example(createWorkerWithExistingAddressesExample)
@example(createWorkerWithNewOfficialAddressExample)
@example(createWorkerWithNewPostalAddressExample)
model CreateWorkerWithNewOrganizationRequest {
    ...WorkerFields;
    ...Record<never>;

    @doc("Данные новой организации.")
    organization: OrganizationRequest;
}