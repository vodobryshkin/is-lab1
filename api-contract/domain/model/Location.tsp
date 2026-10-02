namespace WorkersApi.Domain;

const locationExample: Location = #{
    x: 10,
    y: 10,
    z: 10
};

@doc("Данные, которые пользователь передает при создании локации.")
@example(locationExample)
model Location {
    @doc("Координата x локации.")
    x: int32;

    @doc("Координата y локации.")
    y: int32;

    @doc("Координата z локации.")
    z: int64;
}