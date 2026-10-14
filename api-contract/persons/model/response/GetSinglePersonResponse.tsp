import "../../../common/model/PersonFields.tsp";
import "../../../locations/model/response/GetSingleLocationResponse.tsp";

using WorkersApi.Domain;
using WorkersApi.Locations;

namespace WorkersApi.Persons;

const getSinglePersonExample: GetSinglePersonResponse = #{
    ...personExample,
    id: 10,
    location: getSingleLocationExample
};

@doc("Данные персоны.")
@example(getSinglePersonExample)
model GetSinglePersonResponse {
    ...OmitProperties<PersonFields, "location">;

    @doc("ID персоны.")
    @minValue(1)
    id: int64;

    @doc("Локация персоны.")
    location: GetSingleLocationResponse;
}
