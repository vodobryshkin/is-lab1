import "@typespec/http";

import "./operations/create.tsp";
import "./operations/get.tsp";
import "./operations/update.tsp";
import "./operations/delete.tsp";

using TypeSpec.Http;

@route("/addresses")
@tag("Addresses")
@useAuth(WorkersApi.Security.SessionCookieAuth)
namespace WorkersApi.Addresses;