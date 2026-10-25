import "@typespec/http";

import "../common/model/LocationFields.tsp";

import "./operations/create.tsp";
import "./operations/get.tsp";
import "./operations/update.tsp";
import "./operations/delete.tsp";

using TypeSpec.Http;
using WorkersApi.Domain;

@route("/locations")
@tag("Locations")
@useAuth(WorkersApi.Security.SessionCookieAuth)
namespace WorkersApi.Locations;

const locationWriteExample: LocationWriteRequest = #{
    ...locationExample
};

@doc("Данные локации для создания и обновления.")
@example(locationWriteExample)
model LocationWriteRequest {
    ...LocationFields;
    ...Record<never>;
}
