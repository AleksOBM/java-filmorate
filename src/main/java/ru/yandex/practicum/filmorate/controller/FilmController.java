package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
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

        try {
            log.trace("Обработка названия фильма");

            if (film.getName() == null || film.getName().isBlank()) {
                throw new ValidationException("Название не может быть пустым.");
            } else {
                log.trace("Название фильма обработано");
            }

        } catch (ValidationException exception) {
            log.debug(exception.getMessage());
            log.error("Добавление не удалось.");
            throw new RuntimeException(exception.getMessage());
        }

        try {
            log.trace("Обработка описания фильма");

            if (film.getDescription() != null && film.getDescription().length() > 200) {
                throw new ValidationException("Максимальная длина описания — 200 символов.");
            } else {
                log.trace("Описание фильма обработано");
            }

        } catch (ValidationException exception) {
            log.debug(exception.getMessage());
            log.error("Добавление не удалось.");
            throw new RuntimeException(exception.getMessage());
        }

        try {
            log.trace("Обработка даты релиза фильма");

            if (film.getReleaseDate() != null) {
                if (film.getReleaseDate().isBefore(birthdayOfCinema)) {
                    throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года.");
                } else if (film.getReleaseDate().isAfter(LocalDate.now())) {
                    throw new ValidationException("Дата релиза не должна быть в будущем");
                } else {
                    log.trace("Дата релиза обработана");
                }
            } else {
                throw new ValidationException("Дата релиза должна быть");
            }

        } catch (ValidationException exception) {
            log.debug(exception.getMessage());
            log.error("Добавление не удалось.");
            throw new RuntimeException(exception.getMessage());
        }

        try {
            log.trace("Обработка продолжительности фильма");

            if (film.getDuration() != null && film.getDuration() <= 0) {
                throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
            } else {
                log.trace("Продолжительность фильма обработана");
            }

        } catch (ValidationException exception) {
            log.debug(exception.getMessage());
            log.error("Добавление не удалось.");
            throw new RuntimeException(exception.getMessage());
        }

        log.trace("Сохранение нового фильма");
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Новый фильм с id={} успешно добавлен", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {

        log.info("Обновление данных о фильме:");

        try {
            log.trace("Проверка id фильма");

            if (newFilm.getId() == null) {
                throw new ValidationException("Id должен быть указан");
            } else if (!films.containsKey(newFilm.getId())) {
                throw new ValidationException("Пользователь с id = " + newFilm.getId() + " не найден");
            } else {
                log.trace("id фильма проверен");
            }

        } catch (ValidationException exception) {
            log.debug(exception.getMessage());
            log.error("Обновление не удалось.");
            throw new RuntimeException(exception.getMessage());
        }

        log.trace("Получение текущих данных о фильме");
        Film oldFilm = films.get(newFilm.getId());
        log.trace("Текущие данные получены");

        try {
            log.trace("Проверка названия фильма");

            if (newFilm.getName() == null) {
                newFilm.setName(oldFilm.getName());
            } else if (newFilm.getName().isBlank()) {
                throw new ValidationException("Название не может быть пустым.");
            } else {
                log.info("Название фильма изменено");
            }

        } catch (ValidationException exception) {
            log.debug(exception.getMessage());
            log.error("Обновление не удалось.");
            throw new RuntimeException(exception.getMessage());
        }

        try {
            log.trace("Проверка описания фильма");

            if (newFilm.getDescription() == null) {
                newFilm.setDescription(oldFilm.getDescription());
            } else if (newFilm.getDescription().length() > 200) {
                throw new ValidationException("Максимальная длина описания — 200 символов.");
            } else {
                log.info("Описание фильма изменено");
            }

        } catch (ValidationException exception) {
            log.debug(exception.getMessage());
            log.error("Обновление не удалось.");
            throw new RuntimeException(exception.getMessage());
        }

        try {
            log.trace("Проверка даты релиза фильма");

            if (newFilm.getReleaseDate() != null) {
                if (newFilm.getReleaseDate().isBefore(birthdayOfCinema)) {
                    throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года.");
                } else if (newFilm.getReleaseDate().isAfter(LocalDate.now())) {
                    throw new ValidationException("Дата релиза не должна быть в будущем");
                } else {
                    log.info("Дата релиза изменена");
                }
            } else {
                newFilm.setReleaseDate(oldFilm.getReleaseDate());
            }

        } catch (ValidationException exception) {
            log.debug(exception.getMessage());
            log.error("Обновление не удалось.");
            throw new RuntimeException(exception.getMessage());
        }

        try {
            log.trace("Проверка продолжительности фильма");

            if (newFilm.getDuration() == null) {
                newFilm.setDuration(oldFilm.getDuration());
                log.info("Продолжительность фильма изменена");
            } else if (newFilm.getDuration() <= 0) {
                throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
            }

        } catch (ValidationException exception) {
            log.debug(exception.getMessage());
            log.error("Обновление не удалось.");
            throw new RuntimeException(exception.getMessage());
        }

        log.trace("Сохранение новых данных о фильме");
        oldFilm = newFilm.toBuilder().build();
        films.put(oldFilm.getId(), oldFilm);
        log.info("Данные фильма с id={} успешно обновлены", oldFilm.getId());
        return oldFilm;
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
