import "../../../common/model/LocationFields.tsp";

using WorkersApi.Domain;

namespace WorkersApi.Locations;

const getSingleLocationExample: GetSingleLocationResponse = #{
    ...locationExample,
    id: 7
};

@doc("Данные локации.")
@example(getSingleLocationExample)
model GetSingleLocationResponse {
    ...LocationFields;

    @doc("ID локации.")
    @minValue(1)
    id: int32;
}
