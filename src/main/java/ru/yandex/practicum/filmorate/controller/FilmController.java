package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/films")
@RestController
@Slf4j
public class FilmController {

    private final LocalDate birthdayOfCinema = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Поиск всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Добавление нового фильма:");

        log.trace("Проверка названия нового фильма");
        if (film.getName() == null) {
            throw new ValidationException("Название должно быть указано.");
        }

        log.trace("Проверка даты релиза нового фильма");
        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата релиза должна быть указана.");
        }

        log.trace("Проверка продолжительности нового фильма");
        if (film.getDuration() == null) {
            throw new ValidationException("Продолжительность фильма должна быть указана.");
        }

        filmValidate(film);

        log.trace("Сохранение нового фильма");
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.info("Обновление данных о фильме:");

        if (film == null) {
            throw new ValidationException("Входные данные фильма не распознаны.");
        }

        log.trace("Проверка id фильма");
        if (film.getId() == null) {
            throw new ValidationException("Id должен быть указан.");
        } else if (!films.containsKey(film.getId())) {
            throw new ValidationException("Пользователь с id = " + film.getId() + " не найден.");
        }

        filmValidate(film);

        log.trace("Получение текущих данных о фильме");
        Film oldFilm = films.get(film.getId());

        log.trace("Обработка названия фильма");
        if (film.getName() == null) {
            film.setName(oldFilm.getName());
        } else {
            log.info("Название фильма изменено.");
        }

        log.trace("Обработка описания фильма");
        if (film.getDescription() == null) {
            film.setDescription(oldFilm.getDescription());
        } else {
            log.info("Описание фильма изменено.");
        }

        log.trace("Обработка даты релиза фильма");
        if ((film.getReleaseDate() == null)) {
            film.setReleaseDate(oldFilm.getReleaseDate());
        } else {
            log.info("Дата релиза изменена.");
        }

        log.trace("Обработка продолжительности фильма");
        if (film.getDuration() == null) {
            film.setDuration(oldFilm.getDuration());
        } else {
            log.info("Продолжительность фильма изменена.");
        }

        log.trace("Сохранение новых данных о фильме");
        films.put(film.getId(), film);
        return film;
    }

    private void filmValidate(Film film) {
        log.info("Проверка фильма:");

        log.trace("Проверка названия фильма");
        if (film.getName() != null && film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым.");
        }

        log.trace("Проверка описания фильма");
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов.");
        }

        log.trace("Проверка даты релиза фильма");
        if (film.getReleaseDate() != null) {
            if (film.getReleaseDate().isBefore(birthdayOfCinema)) {
                throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года.");
            } else if (film.getReleaseDate().isAfter(LocalDate.now())) {
                throw new ValidationException("Дата релиза не должна быть в будущем.");
            }
        }

        log.trace("Проверка продолжительности фильма");
        if (film.getDuration() != null && film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }
    }

    private int getNextId() {
        log.trace("Генерация нового id фильма");
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(Math::toIntExact)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
