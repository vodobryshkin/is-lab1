import "@typespec/openapi3";
import "./CoordinatesFields.tsp";

using TypeSpec.OpenAPI;

namespace WorkersApi.Domain;

const existingCoordinatesExample = #{ id: 5 };

@doc("Новые координаты или ID существующих.")
@oneOf
@example(coordinatesExample)
@example(existingCoordinatesExample)
union CoordinatesRequest {
    new_coordinates: {
        ...CoordinatesFields;
        ...Record<never>;
    },

    existing_coordinates: {
        ...Record<never>;

        @doc("ID координат.")
        @minValue(1)
        id: int64;
    },
}
