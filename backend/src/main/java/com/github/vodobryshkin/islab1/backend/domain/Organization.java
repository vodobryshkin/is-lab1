package com.github.vodobryshkin.islab1.backend.domain;

/**
 * @author vodobryshkin
 * @since 18.09.2026 14:53
 */
public class Organization {
    private Address officialAddress; //Поле не может быть null
    private float annualTurnover;    //Значение поля должно быть больше 0
    private Long employeesCount;     //Поле не может быть null, Значение поля должно быть больше 0
    private String fullName;         //Значение этого поля должно быть уникальным, Строка не может быть пустой, Поле не может быть null
    private Integer rating;          //Поле не может быть null, Значение поля должно быть больше 0
    private OrganizationType type;   //Поле может быть null
    private Address postalAddress;   //Поле не может быть null
}
