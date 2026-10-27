namespace WorkersApi.Errors;

const validationIssueExample: ValidationIssue = #{
    field: "salary",
    code: "MUST_BE_POSITIVE",
    message: "salary must be greater than zero."
};

@doc("Нарушение правила в одном поле тела запроса.")
@example(validationIssueExample)
model ValidationIssue {
    @doc("Имя поля; у вложенного поля -- путь через точку, у всего тела -- `$`.")
    field: string;

    @doc("Машинный код нарушения.")
    code: string;

    @doc("Понятное описание нарушения.")
    message: string;
}
