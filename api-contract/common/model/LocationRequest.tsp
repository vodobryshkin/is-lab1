import "@typespec/openapi3";
import "./LocationFields.tsp";

using TypeSpec.OpenAPI;

namespace WorkersApi.Domain;

const existingLocationExample = #{ id: 7 };

@doc("Новая локация или ID существующей.")
@oneOf
@example(locationExample)
@example(existingLocationExample)
union LocationRequest {
    new_location: {
        ...LocationFields;
        ...Record<never>;
    },

    existing_location: {
        ...Record<never>;

        @doc("ID локации.")
        @minValue(1)
        id: int32;
    },
}
