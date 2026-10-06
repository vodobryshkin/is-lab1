import "@typespec/http";

import "./model/request/CreateWorkerRequest.tsp";
import "./model/request/UpdateWorkerRequest.tsp";

import "./operations/get.tsp";
import "./operations/create.tsp";

using TypeSpec.Http;

@route("/workers")
@tag("Workers")
namespace WorkersApi.Workers;