import "@typespec/http";

import "./model/request/CreateWorkerRequest.tsp";
import "./model/request/UpdateWorkerRequest.tsp";


using TypeSpec.Http;

@route("/workers")
@tag("Workers")
namespace WorkersApi.Worker;