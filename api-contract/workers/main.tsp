import "@typespec/http";

import "./model/request/CreateWorkerRequest.tsp";
import "./model/request/UpdateWorkerRequest.tsp";

import "./operations/get.tsp";
import "./operations/create.tsp";
import "./operations/update.tsp";
import "./operations/delete.tsp";
import "./operations/count_by_status.tsp";
import "./operations/rating_less_than.tsp";
import "./operations/hire.tsp";

using TypeSpec.Http;

@route("/workers")
@tag("Workers")
namespace WorkersApi.Workers;