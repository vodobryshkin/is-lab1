package com.github.vodobryshkin.islab1.backend.resource;

import com.github.vodobryshkin.islab1.backend.domain.Address;
import com.github.vodobryshkin.islab1.backend.domain.Color;
import com.github.vodobryshkin.islab1.backend.domain.Coordinates;
import com.github.vodobryshkin.islab1.backend.domain.Country;
import com.github.vodobryshkin.islab1.backend.domain.Location;
import com.github.vodobryshkin.islab1.backend.domain.Organization;
import com.github.vodobryshkin.islab1.backend.domain.OrganizationType;
import com.github.vodobryshkin.islab1.backend.domain.Person;
import com.github.vodobryshkin.islab1.backend.domain.Position;
import com.github.vodobryshkin.islab1.backend.domain.Status;
import com.github.vodobryshkin.islab1.backend.domain.Worker;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * Собирает сущности из полей тела запроса.
 * У вложенной ссылки по ID задан только ID, её поля нового объекта не проверяются.
 */
@ApplicationScoped
public class RequestEntities {
    private static final String ID = "id";
    private static final String ORGANIZATION_FIELD = "organization";
    private static final String ORGANIZATION_ID_FIELD = "organization_id";
    private static final String RATING = "rating";
    private static final String MUTUALLY_EXCLUSIVE = "MUTUALLY_EXCLUSIVE";
    private static final List<String> PERSON_FIELDS = List.of("eye_color", "hair_color",
            "location", "birthday", "weight", "nationality");

    /**
     * Читает локацию: новую {x, y, z} или ссылку {id}.
     *
     * @param fields поля родительского объекта
     * @param field имя поля
     * @param nullable допускается ли null
     * @return локация или null
     */
    public Location location(RequestFields fields, String field, boolean nullable) {
        RequestFields location = fields.object(field, nullable);
        if (location == null) {
            return null;
        }
        if (location.isReference("x", "y", "z")) {
            return new Location(location.id(ID), null, null, null);
        }
        return new Location(null, location.int32("x"), location.int32("y"), location.int64("z"));
    }

    /**
     * Читает координаты: новые {x, y} или ссылку {id}.
     *
     * @param fields поля родительского объекта
     * @param field имя поля
     * @return координаты или null, если поле неверно
     */
    public Coordinates coordinates(RequestFields fields, String field) {
        RequestFields coordinates = fields.object(field, false);
        if (coordinates == null) {
            return null;
        }
        if (coordinates.isReference("x", "y")) {
            return new Coordinates(coordinates.id(ID), null, null);
        }
        return new Coordinates(null, coordinates.int32("x"), coordinates.int64("y"));
    }

    /**
     * Читает адрес в варианте «новый адрес в objectField или ID существующего
     * в idField»; нужно ровно одно из двух полей.
     *
     * @param fields поля родительского объекта
     * @param objectField поле нового адреса {street, town}
     * @param idField поле ID существующего адреса
     * @return адрес или null, если поля неверны
     */
    public Address address(RequestFields fields, String objectField, String idField) {
        if (fields.has(objectField) && fields.has(idField)) {
            String description = "Use either " + objectField + " or " + idField + ".";
            fields.reject(objectField, MUTUALLY_EXCLUSIVE, description);
            fields.reject(idField, MUTUALLY_EXCLUSIVE, description);
            return null;
        }
        if (fields.has(idField)) {
            fields.markReference(objectField);
            return new Address(fields.id(idField), null, null);
        }
        RequestFields address = fields.object(objectField, false);
        if (address == null) {
            return null;
        }
        address.allowOnly("street", "town");
        return new Address(null, address.string("street"), location(address, "town", true));
    }

    /**
     * Читает объект как организацию с вариантами адресов из address.
     *
     * @param fields поля организации
     * @return организация без ID
     */
    public Organization organization(RequestFields fields) {
        fields.allowOnly("official_address", "official_address_id", "annual_turnover",
                "employees_count", "full_name", RATING, "type", "postal_address",
                "postal_address_id");
        Float turnover = fields.float32("annual_turnover");
        return new Organization(null, address(fields, "official_address", "official_address_id"),
                turnover == null ? 0 : turnover, fields.int64("employees_count"),
                fields.string("full_name"), fields.int32(RATING),
                fields.enumValue("type", OrganizationType.class, true),
                address(fields, "postal_address", "postal_address_id"));
    }

    /**
     * Читает объект как новую персону без работника.
     *
     * @param fields поля персоны
     * @return персона без ID
     */
    public Person person(RequestFields fields) {
        fields.allowOnly(PERSON_FIELDS.toArray(String[]::new));
        return new Person(null, fields.enumValue("eye_color", Color.class, true),
                fields.enumValue("hair_color", Color.class, false),
                location(fields, "location", false), fields.dateTime("birthday"),
                fields.float32("weight"), fields.enumValue("nationality", Country.class, false),
                null);
    }

    /**
     * Читает персону: новую или ссылку {id}.
     *
     * @param fields поля родительского объекта
     * @param field имя поля
     * @param nullable допускается ли null
     * @return персона или null
     */
    public Person person(RequestFields fields, String field, boolean nullable) {
        RequestFields person = fields.object(field, nullable);
        if (person == null) {
            return null;
        }
        if (person.isReference(PERSON_FIELDS.toArray(String[]::new))) {
            return new Person(person.id(ID), null, null, null, null, null, null, null);
        }
        return person(person);
    }

    /**
     * Читает объект как работника. ID, создателя, дату создания и привязку персоны задаёт
     * сервер. Организация -- null, ссылка (задан только ID) или новая без ID.
     *
     * @param fields поля работника
     * @param update true для PUT: обязателен organization_id, допускающий
     *     null, а новой организации нет; иначе -- три формы создания
     * @return работник без ID, создателя и даты создания
     */
    public Worker worker(RequestFields fields, boolean update) {
        List<String> allowed = new ArrayList<>(List.of("name", "coordinates", "salary", RATING,
                "start_date", "position", "status", "person", ORGANIZATION_ID_FIELD));
        if (!update) {
            allowed.add(ORGANIZATION_FIELD);
        }
        fields.allowOnly(allowed.toArray(String[]::new));
        Double salary = fields.float64("salary");
        return new Worker(null, null, fields.string("name"), coordinates(fields, "coordinates"),
                null, update ? updatedOrganization(fields) : newWorkerOrganization(fields),
                salary == null ? 0 : salary, fields.int32(RATING), fields.dateTime("start_date"),
                fields.enumValue("position", Position.class, true),
                fields.enumValue("status", Status.class, true), person(fields, "person", true));
    }

    /**
     * Создание: organization_id, organization или ни одного из них.
     */
    private Organization newWorkerOrganization(RequestFields fields) {
        if (fields.has(ORGANIZATION_ID_FIELD) && fields.has(ORGANIZATION_FIELD)) {
            String description = "Use either organization_id or organization.";
            fields.reject(ORGANIZATION_ID_FIELD, MUTUALLY_EXCLUSIVE, description);
            fields.reject(ORGANIZATION_FIELD, MUTUALLY_EXCLUSIVE, description);
            return null;
        }
        if (fields.has(ORGANIZATION_ID_FIELD)) {
            return organizationReference(fields);
        }
        if (!fields.has(ORGANIZATION_FIELD)) {
            return null;
        }
        RequestFields organization = fields.object(ORGANIZATION_FIELD, false);
        return organization == null ? null : organization(organization);
    }

    /**
     * PUT: ключ organization_id обязателен; null -- без организации.
     */
    private Organization updatedOrganization(RequestFields fields) {
        return fields.hasNull(ORGANIZATION_ID_FIELD) ? null : organizationReference(fields);
    }

    private Organization organizationReference(RequestFields fields) {
        fields.markReference(ORGANIZATION_FIELD);
        return new Organization(fields.id(ORGANIZATION_ID_FIELD), null, 0, null, null, null,
                null, null);
    }
}
