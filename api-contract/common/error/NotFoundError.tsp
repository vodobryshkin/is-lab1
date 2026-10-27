import "./ErrorResponse.tsp";

namespace WorkersApi.Errors;

const workerNotFoundExample: NotFoundError = #{
    statusCode: 404,
    contentType: "application/json",
    body: #{
        message: "Worker with id=999 does not exist."
    }
};

const workerNotFoundOperationExample = #{
    returnType: workerNotFoundExample
};

const organizationNotFoundExample: NotFoundError = #{
    statusCode: 404,
    contentType: "application/json",
    body: #{
        message: "Organization with id=999 does not exist."
    }
};

const organizationNotFoundOperationExample = #{
    returnType: organizationNotFoundExample
};

const addressNotFoundExample: NotFoundError = #{
    statusCode: 404,
    contentType: "application/json",
    body: #{
        message: "Address with id=999 does not exist."
    }
};

const addressNotFoundOperationExample = #{
    returnType: addressNotFoundExample
};

const coordinatesNotFoundExample: NotFoundError = #{
    statusCode: 404,
    contentType: "application/json",
    body: #{
        message: "Coordinates with id=999 do not exist."
    }
};

const coordinatesNotFoundOperationExample = #{
    returnType: coordinatesNotFoundExample
};

const locationNotFoundExample: NotFoundError = #{
    statusCode: 404,
    contentType: "application/json",
    body: #{
        message: "Location with id=999 does not exist."
    }
};

const locationNotFoundOperationExample = #{
    returnType: locationNotFoundExample
};

const personNotFoundExample: NotFoundError = #{
    statusCode: 404,
    contentType: "application/json",
    body: #{
        message: "Person with id=999 does not exist."
    }
};

const personNotFoundOperationExample = #{
    returnType: personNotFoundExample
};

@error
@doc("Ошибка: объект из пути или объект, на который ссылается запрос, не найден.")
@example(workerNotFoundExample)
model NotFoundError is ErrorResult<404>;
