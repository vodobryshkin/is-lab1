import "./ProblemResponse.tsp";

namespace WorkersApi.Errors;

const conflictExample: ConflictError = #{
    statusCode: 409,
    contentType: "application/json",
    body: #{
        type: "/problems/organization-has-workers",
        title: "Organization cannot be deleted",
        status: 409,
        detail: "The organization cannot be deleted while workers reference it.",
        instance: "/problem-occurrences/example-409"
    }
};

@error
@doc("Ошибка: конфликт с текущим состоянием ресурса.")
model ConflictError<TBody = ProblemDetails<409>> is ProblemResponse<409, TBody>;

@doc("Название организации занято.")
model OrganizationNameConflictDetails {
    ...OmitProperties<ProblemDetails<409>, "type">;
    type: "/problems/organization-name-already-exists";

    @doc("Занятое название организации.")
    full_name: string;
}

@doc("Персона связана с другим работником.")
model PersonAlreadyAssignedDetails {
    ...OmitProperties<ProblemDetails<409>, "type">;
    type: "/problems/person-already-assigned";

    @doc("ID занятой персоны.")
    @minValue(1)
    person_id: int32;
}

const personAlreadyAssignedExample:
    ConflictError<PersonAlreadyAssignedDetails> = #{
        statusCode: 409,
        contentType: "application/json",
        body: #{
            type: "/problems/person-already-assigned",
            title: "Person already assigned",
            status: 409,
            detail: "Person with id=10 is already assigned to another worker.",
            person_id: 10
        }
    };

const personAlreadyAssignedOperationExample = #{
    returnType: personAlreadyAssignedExample
};
