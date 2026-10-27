import "./LocationValidationIssue.tsp";

namespace WorkersApi.Errors;

@doc("Нарушения полей адреса и вложенной локации.")
union AddressValidationIssue<path extends string, separator extends string = "."> {
    street: {
        field: "${path}${separator}street";
        code: "MUST_NOT_BE_EMPTY";
        message: string;
    },

    town_fields: LocationValidationIssue<"${path}${separator}town">,
}
