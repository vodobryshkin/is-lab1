package com.github.vodobryshkin.islab1.backend.domain;

/**
 * @author vodobryshkin
 * @since 18.09.2026 14:55
 */
public class Person {
    private Color eyeColor;                   //Поле может быть null
    private Color hairColor;                  //Поле не может быть null
    private Location location;                //Поле не может быть null
    private java.time.ZonedDateTime birthday; //Поле не может быть null
    private Float weight;                     //Поле не может быть null, Значение поля должно быть больше 0
    private Country nationality;              //Поле не может быть null
}
