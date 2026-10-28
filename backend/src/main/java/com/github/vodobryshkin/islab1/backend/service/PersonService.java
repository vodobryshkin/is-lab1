package com.github.vodobryshkin.islab1.backend.service;

import com.github.vodobryshkin.islab1.backend.domain.Location;
import com.github.vodobryshkin.islab1.backend.domain.Person;
import com.github.vodobryshkin.islab1.backend.dto.common.ListQuery;
import com.github.vodobryshkin.islab1.backend.events.EventSessions;
import com.github.vodobryshkin.islab1.backend.repository.PersonRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Операции с персонами. Локация персоны -- новая или существующая, связь с работником
 * задаётся со стороны работника и здесь не меняется.
 */
@ApplicationScoped
public class PersonService {
    private final PersonRepository repository;
    private final LocationService locationService;
    private final EventSessions events;

    /**
     * Создаёт сервис.
     *
     * @param repository хранилище персон
     * @param locationService локации
     * @param events уведомление об изменениях после фиксации транзакции
     */
    @Inject
    public PersonService(PersonRepository repository, LocationService locationService,
            EventSessions events) {
        this.repository = repository;
        this.locationService = locationService;
        this.events = events;
    }

    /**
     * Нужен CDI для создания клиентского прокси, прикладной код его не вызывает.
     */
    protected PersonService() {
        this.repository = null;
        this.locationService = null;
        this.events = null;
    }

    /**
     * Сохраняет персону без работника и новую локацию в одной транзакции.
     *
     * @param person персона, у локации задан только ID, если она существующая
     * @return ID персоны или пустой результат, если локации с таким ID нет
     */
    @Transactional
    public Optional<Long> create(Person person) {
        Optional<Location> location = locationService.persistOrFind(person.getLocation());

        if (location.isEmpty()) {
            return Optional.empty();
        }

        person.setLocation(location.get());
        person.setWorker(null);
        repository.save(person);
        events.changed();

        return Optional.of(person.getId());
    }

    /**
     * Ищет персону.
     *
     * @param id ID персоны
     * @return персона или пустой результат
     */
    public Optional<Person> find(long id) {
        return repository.findById(id);
    }

    /**
     * Возвращает страницу персон с фильтрами по подстроке.
     *
     * @param query параметры страницы
     * @param eyeColor подстрока цвета глаз или null
     * @param hairColor подстрока цвета волос или null
     * @param nationality подстрока национальности или null
     * @return персоны страницы
     */
    public List<Person> findPage(ListQuery query, String eyeColor, String hairColor,
            String nationality) {
        return repository.findPage(query, eyeColor, hairColor, nationality);
    }

    /**
     * Считает персоны с фильтрами по подстроке.
     *
     * @param eyeColor подстрока цвета глаз или null
     * @param hairColor подстрока цвета волос или null
     * @param nationality подстрока национальности или null
     * @return количество
     */
    public long count(String eyeColor, String hairColor, String nationality) {
        return repository.count(eyeColor, hairColor, nationality);
    }

    /**
     * Заменяет поля и локацию персоны, связь с работником сохраняется,
     * прежняя локация не удаляется.
     *
     * @param id ID персоны
     * @param values новые значения
     * @return DONE, NOT_FOUND, REFERENCE_NOT_FOUND
     */
    @Transactional
    public Outcome update(long id, Person values) {
        Optional<Person> person = repository.findById(id);

        if (person.isEmpty()) {
            return Outcome.NOT_FOUND;
        }

        Optional<Location> location = locationService.persistOrFind(values.getLocation());

        if (location.isEmpty()) {
            return Outcome.REFERENCE_NOT_FOUND;
        }

        Person target = person.get();
        target.setEyeColor(values.getEyeColor());
        target.setHairColor(values.getHairColor());
        target.setLocation(location.get());
        target.setBirthday(values.getBirthday());
        target.setWeight(values.getWeight());
        target.setNationality(values.getNationality());
        events.changed();

        return Outcome.DONE;
    }

    /**
     * Удаляет персону, если она не связана с работником.
     * Внешний ключ такому удалению не мешает, поэтому запрет обеспечивает эта проверка.
     *
     * @param id ID персоны
     * @return DONE, NOT_FOUND, IN_USE
     */
    @Transactional
    public Outcome delete(long id) {
        Optional<Person> person = repository.findById(id);

        if (person.isEmpty()) {
            return Outcome.NOT_FOUND;
        }

        if (person.get().getWorker() != null) {
            return Outcome.IN_USE;
        }

        repository.delete(person.get());
        events.changed();

        return Outcome.DONE;
    }
}
