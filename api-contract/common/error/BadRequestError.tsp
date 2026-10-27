import "./ErrorResponse.tsp";

namespace WorkersApi.Errors;

const badRequestExample: BadRequestError = #{
    statusCode: 400,
    contentType: "application/json",
    body: #{
        message: "The request body contains invalid JSON."
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
    Ошибка: запрос невозможно обработать. Тело содержит некорректный JSON
    или параметр пути либо запроса неверен; сообщение называет параметр и причину.
    """)
@example(badRequestExample)
model BadRequestError is ErrorResult<400>;
