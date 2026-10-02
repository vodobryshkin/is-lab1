namespace WorkersApi.Errors;

@doc("Отдельное нарушение правила валидации.")
model ValidationIssue {
    @doc("Название поля, в котором возникла ошибка валидации.")
    field: string;

    @doc("Короткое enum-обозначение ошибки")
    title: string;

    @doc("Описание ошибки валидации в конкретном поле.")
    description: string;
}