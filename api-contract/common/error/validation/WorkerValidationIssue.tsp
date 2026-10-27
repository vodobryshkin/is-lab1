import "./CoordinatesValidationIssue.tsp";
import "./PersonValidationIssue.tsp";

namespace WorkersApi.Errors;

@doc("Нарушения полей работника, координат и персоны.")
union WorkerValidationIssue {
    name: {
        field: "name";
        code: "MUST_NOT_BE_EMPTY" | "MUST_MATCH_PATTERN" | "OUT_OF_RANGE";
        message: string;
    },

    coordinates: {
        field: "coordinates";
        code: "MUST_NOT_BE_NULL";
        message: string;
    },

    salary: {
        field: "salary";
        code: "MUST_NOT_BE_NULL" | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        message: string;
    },

    rating: {
        field: "rating";
        code: "MUST_NOT_BE_NULL" | "MUST_BE_POSITIVE";
        message: string;
    },

    start_date: {
        field: "start_date";
        code: "MUST_NOT_BE_NULL";
        message: string;
    },

    coordinates_fields: CoordinatesValidationIssue<"coordinates">,

    person_fields: PersonValidationIssue<"person">,
}
