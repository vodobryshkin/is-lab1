import "@typespec/http";

import "./model/CreateWorkerRequest.tsp";
import "./model/CreateWorkerWithExistingOrganizationRequest.tsp";
import "./model/CreateWorkerWithNewOrganizationRequest.tsp";
import "./model/CreateWorkerWithoutOrganizationRequest.tsp";


using TypeSpec.Http;

@route("/workers")
namespace WorkersApi.Worker;