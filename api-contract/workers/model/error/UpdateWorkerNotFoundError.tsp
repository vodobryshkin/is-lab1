import "../../../common/error/ProblemResponse.tsp";
import "../../../common/error/NotFoundError.tsp";
import "./WorkerNotFoundError.tsp";
import "./CreateWorkerOrganizationNotFoundError.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Workers.Errors;

@error
@doc("Работник или связанный объект не найдены.")
@example(workerNotFoundErrorExample)
@example(createWorkerOrganizationNotFoundErrorExample)
@example(coordinatesNotFoundExample)
@example(locationNotFoundExample)
@example(personNotFoundExample)
model UpdateWorkerNotFoundError is NotFoundError<
    WorkerNotFoundDetails
    | OrganizationNotFoundDetails
    | CoordinatesNotFoundDetails
    | LocationNotFoundDetails
    | PersonNotFoundDetails
>;
