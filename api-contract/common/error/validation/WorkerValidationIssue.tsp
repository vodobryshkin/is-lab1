import "./CoordinatesValidationIssue.tsp";
import "./PersonValidationIssue.tsp";

namespace WorkersApi.Errors;

@doc("Нарушения полей работника, координат и персоны. Вложенные поля проверяются только при наличии объекта.")
union WorkerValidationIssue {
    name: {
        field: "name";
        title: "required" | "must_not_be_null" | "invalid_type"
            | "must_not_be_empty" | "must_match_pattern";
        description: string;
    },

    coordinates: {
        field: "coordinates";
        title: "required" | "must_not_be_null" | "invalid_type";
        description: string;
    },

    salary: {
        field: "salary";
        title: "required" | "must_not_be_null" | "invalid_type"
            | "must_be_positive" | "out_of_range";
        description: string;
    },

    rating: {
        field: "rating";
        title: "required" | "must_not_be_null" | "invalid_type"
            | "must_be_positive" | "out_of_range";
        description: string;
    },

    start_date: {
        field: "start_date";
        title: "required" | "must_not_be_null"
            | "invalid_type" | "invalid_date_time";
        description: string;
    },

    position: {
        field: "position";
        title: "required" | "invalid_type" | "invalid_enum_value";
        description: string;
    },

    status: {
        field: "status";
        title: "required" | "invalid_type" | "invalid_enum_value";
        description: string;
    },

    person: {
        field: "person";
        title: "required" | "invalid_type";
        description: string;
    },

    coordinates_fields: CoordinatesValidationIssue<"coordinates">,

    person_fields: PersonValidationIssue<"person">,
}