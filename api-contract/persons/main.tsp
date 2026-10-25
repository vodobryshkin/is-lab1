import "@typespec/http";

import "../common/model/PersonFields.tsp";

import "./operations/create.tsp";
import "./operations/get.tsp";
import "./operations/update.tsp";
import "./operations/delete.tsp";

using TypeSpec.Http;
using WorkersApi.Domain;

@route("/persons")
@tag("Persons")
@useAuth(WorkersApi.Security.SessionCookieAuth)
namespace WorkersApi.Persons;

const personWriteExample: PersonWriteRequest = #{
    ...personExample
};

@doc("Данные персоны для создания и обновления.")
@example(personWriteExample)
model PersonWriteRequest {
    ...PersonFields;
    ...Record<never>;
}
