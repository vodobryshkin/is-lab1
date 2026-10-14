import "@typespec/http";

import "../common/model/CoordinatesFields.tsp";

import "./operations/create.tsp";
import "./operations/get.tsp";
import "./operations/update.tsp";
import "./operations/delete.tsp";

using TypeSpec.Http;
using WorkersApi.Domain;

@route("/coordinates")
@tag("Coordinates")
@useAuth(BearerAuth)
namespace WorkersApi.Coordinates;

const coordinatesWriteExample: CoordinatesWriteRequest = #{
    ...coordinatesExample
};

@doc("Данные координат для создания и обновления.")
@example(coordinatesWriteExample)
model CoordinatesWriteRequest {
    ...CoordinatesFields;
    ...Record<never>;
}
