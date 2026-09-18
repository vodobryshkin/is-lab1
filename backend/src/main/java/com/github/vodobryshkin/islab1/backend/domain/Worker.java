package com.github.vodobryshkin.islab1.backend.domain;

/**
 * @author vodobryshkin
 * @since 18.09.2026 14:52
 */
public class Worker {
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Organization organization; //Поле может быть null
    private double salary; //Значение поля должно быть больше 0
    private Integer rating; //Поле не может быть null, Значение поля должно быть больше 0
    private java.time.ZonedDateTime startDate; //Поле не может быть null
    private Position position; //Поле может быть null
    private Status status; //Поле может быть null
    private Person person; //Поле может быть null
}
