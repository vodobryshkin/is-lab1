import "../../../common/model/CoordinatesFields.tsp";

using WorkersApi.Domain;

namespace WorkersApi.Coordinates;

const getSingleCoordinatesExample: GetSingleCoordinatesResponse = #{
    ...coordinatesExample,
    id: 5
};

@doc("Данные координат.")
@example(getSingleCoordinatesExample)
model GetSingleCoordinatesResponse {
    ...CoordinatesFields;

    @doc("ID координат.")
    @minValue(1)
    id: int32;
}
