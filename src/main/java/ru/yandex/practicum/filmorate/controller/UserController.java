package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Поиск всех пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {

        log.info("Добавления нового пользователя:");

        try {
            log.trace("Обработка электронной почты");

            if (user.getEmail() == null || user.getEmail().isBlank()) {
                throw new ValidationException("Электронная почта не может быть пустой.");
            } else if (!user.getEmail().contains("@")) {
                throw new ValidationException("Электронная почта должна содержать символ '@'.");
            } else if (users.values().stream().map(User::getEmail).anyMatch(u -> u.equals(user.getEmail()))) {
                throw new ValidationException("Эта электронная почта уже используется.");
            } else {
                log.trace("Электронная почта обработана");
            }

        } catch (ValidationException exception) {
            log.debug(exception.getMessage());
            log.error("Добавление не удалось.");
            throw new RuntimeException(exception.getMessage());
        }

        try {
            log.trace("Обработка логина");

            if (user.getLogin() == null || user.getLogin().isBlank()) {
                throw new ValidationException("Логин не может быть пустым.");
            } else if (user.getLogin().contains(" ")) {
                throw new ValidationException("Логин не должен содержать пробелов.");
            } else {
                log.trace("Логин обработан");
            }

        } catch (ValidationException exception) {
            log.debug(exception.getMessage());
            log.error("Добавление не удалось.");
            throw new RuntimeException(exception.getMessage());
        }

        log.trace("Обработка имени пользователя");
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.trace("Пользователю присвоено имя из логина");
        } else {
            log.trace("Имя пользователя обработано");
        }

        try {
            log.trace("Обработка даты рождения");

            if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
                throw new ValidationException("Дата рождения не может быть в будущем.");
            } else {
                log.trace("Дата рождения обработана");
            }

        } catch (ValidationException exception) {
            log.debug(exception.getMessage());
            log.error("Добавление не удалось.");
            throw new RuntimeException(exception.getMessage());
        }

        log.trace("Сохранение нового пользователя");
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Новый пользователь с id={} успешно добавлен", user.getId());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {

        log.info("Обновление данных о пользователе:");

        try {
            log.trace("Проверка id пользователя");

            if (newUser.getId() == null) {
                throw new ValidationException("Id должен быть указан");
            } else if (!users.containsKey(newUser.getId())) {
                throw new ValidationException("Пользователь с id = " + newUser.getId() + " не найден");
            } else {
                log.trace("id пользователя проверен");
            }

        } catch (ValidationException exception) {
            log.debug(exception.getMessage());
            log.error("Обновление не удалось.");
            throw new RuntimeException(exception.getMessage());
        }

        log.trace("Получение текущих данных о пользователе");
        User oldUser = users.get(newUser.getId());
        log.trace("Текущие данные получены");

        try {
            log.trace("Проверка логина");

            if (newUser.getLogin() == null) {
                newUser.setLogin(oldUser.getLogin());
            } else if (newUser.getLogin().isBlank()) {
                throw new ValidationException("Логин не может быть пустым.");
            } else if (newUser.getLogin().contains(" ")) {
                throw new ValidationException("Логин не должен содержать пробелов.");
            } else {
                log.info("Логин пользователя изменен");
            }

        } catch (ValidationException exception) {
            log.debug(exception.getMessage());
            log.error("Обновление не удалось.");
            throw new RuntimeException(exception.getMessage());
        }

            log.trace("Проверка имени пользователя");
            if (newUser.getName() == null) {
                newUser.setName(oldUser.getName());
            } else if (newUser.getName().isBlank()) {
                newUser.setName(newUser.getLogin());
            } else {
                log.info("Имя пользователя изменено");
            }

        try {
            log.trace("Проверка электронной почты");

            if (newUser.getEmail() == null) {
                newUser.setEmail(oldUser.getEmail());
            } else if (newUser.getEmail().isBlank()) {
                throw new ValidationException("Электронная почта не может быть пустой.");
            } else if (!newUser.getEmail().contains("@")) {
                throw new ValidationException("Электронная почта должна содержать символ '@'.");
            } else if (users.values().stream()
                    .filter(user -> !(user.getId().equals(newUser.getId())))
                    .map(User::getEmail)
                    .anyMatch(u -> u.equals(newUser.getEmail()))) {
                throw new ValidationException("Эта электронная почта уже используется.");
            } else {
                log.info("Электронная почта изменена");
            }

        } catch (ValidationException exception) {
            log.debug(exception.getMessage());
            log.error("Обновление не удалось.");
            throw new RuntimeException(exception.getMessage());
        }

        log.trace("Поверка даты рождения");
        if (newUser.getBirthday() == null) {
            newUser.setBirthday(oldUser.getBirthday());
        } else {
            log.info("Дата рождения изменена");
        }

        log.trace("Сохранение новых данных пользователя");
        oldUser = newUser.toBuilder().build();
        users.put(oldUser.getId(), oldUser);
        log.info("Данные пользователя с id={} успешно обновлены", oldUser.getId());
        return oldUser;
    }

    private int getNextId() {
        log.trace("Генерация нового id пользователя");
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(Math::toIntExact)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
