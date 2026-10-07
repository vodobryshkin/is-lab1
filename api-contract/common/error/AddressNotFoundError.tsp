import "./ProblemResponse.tsp";

namespace WorkersApi.Errors;

const addressNotFoundErrorExample: AddressNotFoundError = #{
    statusCode: 404,
    contentType: "application/json",
    body: #{
        type: "/problems/address-not-found",
        title: "Address not found",
        status: 404,
        detail: "Address with id=999 does not exist."
    }
};

@error
@doc("Адрес не найден.")
@example(addressNotFoundErrorExample)
model AddressNotFoundError is ProblemResponse<
    404,
    {
        ...OmitProperties<ProblemDetails<404>, "type">;

        type: "/problems/address-not-found";
    }
>;