import "../../../common/error/ProblemResponse.tsp";

using WorkersApi.Errors;

namespace WorkersApi.Workers.Errors;

const invalidWorkerIdErrorExample: InvalidWorkerIdError = #{
    statusCode: 400,
    contentType: "application/json",
    body: #{
        type: "/problems/invalid-worker-id",
        title: "Invalid worker ID",
        status: 400,
        errors: #[
            #{
                field: "id",
                title: "MUST_BE_POSITIVE",
                description: "Worker ID must be greater than zero."
            }
        ]
    }
};

@error
@doc("Некорректный ID работника.")
@example(invalidWorkerIdErrorExample)
model InvalidWorkerIdError is ProblemResponse<
    400,
    {
        ...OmitProperties<ProblemDetails<400>, "type">;

        type: "/problems/invalid-worker-id";

        @doc("Ошибки ID работника.")
        @minItems(1)
        errors: {
            field: "id";
            title: "INVALID_TYPE" | "MUST_BE_POSITIVE" | "OUT_OF_RANGE";
            description: string;
        }[];
    }
>;