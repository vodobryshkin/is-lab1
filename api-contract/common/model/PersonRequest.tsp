import "@typespec/openapi3";
import "./PersonFields.tsp";

using TypeSpec.OpenAPI;

namespace WorkersApi.Domain;

const existingPersonExample = #{ id: 10 };

const personWithExistingLocationExample = #{
    ...personExample,
    location: existingLocationExample
};

@doc("Новая персона или ID существующей.")
@oneOf
@example(personExample)
@example(personWithExistingLocationExample)
@example(existingPersonExample)
union PersonRequest {
    new_person: {
        ...PersonFields;
        ...Record<never>;
    },

    existing_person: {
        ...Record<never>;

        @doc("ID персоны.")
        @minValue(1)
        id: int64;
    },
}
