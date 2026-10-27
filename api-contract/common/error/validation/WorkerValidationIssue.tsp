import "./CoordinatesValidationIssue.tsp";
import "./PersonValidationIssue.tsp";

namespace WorkersApi.Errors;

@doc("Нарушения полей работника, координат и персоны. Вложенные поля проверяются только при наличии объекта.")
union WorkerValidationIssue {
    name: {
        field: "name";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_NOT_BE_EMPTY" | "MUST_MATCH_PATTERN" | "OUT_OF_RANGE";
        message: string;
    },

    coordinates: {
        field: "coordinates";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE";
        message: string;
    },

    salary: {
        field: "salary";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        message: string;
    },

    rating: {
        field: "rating";
        code: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        message: string;
    },

    start_date: {
        field: "start_date";
        code: "REQUIRED" | "MUST_NOT_BE_NULL"
            | "INVALID_TYPE" | "INVALID_DATE_TIME";
        message: string;
    },

    position: {
        field: "position";
        code: "REQUIRED" | "INVALID_TYPE" | "INVALID_ENUM_VALUE";
        message: string;
    },

    status: {
        field: "status";
        code: "REQUIRED" | "INVALID_TYPE" | "INVALID_ENUM_VALUE";
        message: string;
    },

    person: {
        field: "person";
        code: "REQUIRED" | "INVALID_TYPE";
        message: string;
    },

    coordinates_fields: CoordinatesValidationIssue<"coordinates">,

    person_fields: PersonValidationIssue<"person">,
}
