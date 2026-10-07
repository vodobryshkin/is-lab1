import "@typespec/http";

import "./operations/register.tsp";
import "./operations/login.tsp";
import "./operations/refresh.tsp";
import "./operations/logout.tsp";

using TypeSpec.Http;

@route("/auth")
@tag("Auth")
@useAuth(NoAuth)
namespace WorkersApi.Auth;