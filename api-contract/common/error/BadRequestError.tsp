import "./ErrorResponse.tsp";

namespace WorkersApi.Errors;

const badRequestExample: BadRequestError = #{
    statusCode: 400,
    contentType: "application/json",
    body: #{
        message: "The request body contains invalid JSON or a value of the wrong type."
    }
};

const badRequestOperationExample = #{
    returnType: badRequestExample
};

const invalidIdExample: BadRequestError = #{
    statusCode: 400,
    contentType: "application/json",
    body: #{
        message: "Parameter id must be greater than zero."
    }
};

const invalidIdOperationExample = #{
    returnType: invalidIdExample
};

const listParameterValidationExample: BadRequestError = #{
    statusCode: 400,
    contentType: "application/json",
    body: #{
        message: "Parameter page_size must not exceed 100."
    }
};

const listParameterValidationOperationExample = #{
    returnType: listParameterValidationExample
};

@error
@doc("""
    Ошибка: запрос невозможно обработать. Тело не является JSON-объектом, содержит
    значение неверного типа или неизвестное значение перечисления, либо неверен
    параметр пути или запроса. Неизвестные поля тела игнорируются.
    """)
@example(badRequestExample)
model BadRequestError is ErrorResult<400>;
