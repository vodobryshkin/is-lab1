namespace WorkersApi.Errors;

@doc("Описание ошибки в формате RFC 9457.")
model ProblemDetails<Status extends int32> {
    @doc("URI-reference, идентифицирующий тип проблемы.")
    type: string;

    @doc("Краткое название типа проблемы.")
    title: string;

    @doc("HTTP-статус, совпадающий со статусом ответа.")
    status: Status;

    @doc("Подробности конкретного случая ошибки.")
    detail?: string;

    @doc("URI-reference, идентифицирующий конкретный случай ошибки.")
    instance?: string;
}