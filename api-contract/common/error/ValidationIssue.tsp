namespace WorkersApi.Errors;

const validationIssueExample: ValidationIssue = #{
    field: "salary",
    title: "MUST_BE_POSITIVE",
    description: "Зарплата должна быть больше нуля."
};

@doc("Отдельное нарушение правила валидации.")
@example(validationIssueExample)
model ValidationIssue {
    @doc("Название поля, в котором возникла ошибка валидации.")
    field: string;

    @doc("Короткое enum-обозначение ошибки")
    title: string;

    @doc("Описание ошибки валидации в конкретном поле.")
    description: string;
}