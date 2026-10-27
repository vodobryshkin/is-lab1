import "./ErrorResponse.tsp";

namespace WorkersApi.Errors;

const personAlreadyAssignedExample: ConflictError = #{
    statusCode: 409,
    contentType: "application/json",
    body: #{
        message: "Person with id=10 is already assigned to another worker."
    }
};

const personAlreadyAssignedOperationExample = #{
    returnType: personAlreadyAssignedExample
};

const organizationNameConflictExample: ConflictError = #{
    statusCode: 409,
    contentType: "application/json",
    body: #{
        message: "Another organization with this full_name already exists."
    }
};

const organizationNameConflictOperationExample = #{
    returnType: organizationNameConflictExample
};

@error
@doc("Ошибка: конфликт с текущим состоянием данных; ничего не изменено.")
@example(organizationNameConflictExample)
model ConflictError is ErrorResult<409>;
