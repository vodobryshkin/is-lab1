import "./CoordinatesValidationIssue.tsp";
import "./PersonValidationIssue.tsp";

namespace WorkersApi.Errors;

@doc("Нарушения полей работника, координат и персоны. Вложенные поля проверяются только при наличии объекта.")
union WorkerValidationIssue {
    name: {
        field: "name";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_NOT_BE_EMPTY" | "MUST_MATCH_PATTERN";
        description: string;
    },

    coordinates: {
        field: "coordinates";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE";
        description: string;
    },

    salary: {
        field: "salary";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        description: string;
    },

    rating: {
        field: "rating";
        title: "REQUIRED" | "MUST_NOT_BE_NULL" | "INVALID_TYPE"
            | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
        description: string;
    },

    start_date: {
        field: "start_date";
        title: "REQUIRED" | "MUST_NOT_BE_NULL"
            | "INVALID_TYPE" | "INVALID_DATE_TIME";
        description: string;
    },

    position: {
        field: "position";
        title: "REQUIRED" | "INVALID_TYPE" | "INVALID_ENUM_VALUE";
        description: string;
    },

    status: {
        field: "status";
        title: "REQUIRED" | "INVALID_TYPE" | "INVALID_ENUM_VALUE";
        description: string;
    },

    person: {
        field: "person";
        title: "REQUIRED" | "INVALID_TYPE";
        description: string;
    },

    coordinates_fields: CoordinatesValidationIssue<"coordinates">,

    person_fields: PersonValidationIssue<"person">,
}