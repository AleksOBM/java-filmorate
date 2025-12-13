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
        userValidate(user);

        log.trace("Проверка логина нового пользователя");
        if (user.getLogin() == null) {
            throw new ValidationException("Логин должен быть указан.");
        }

        log.trace("Проверка даты электронной почты нового пользователя");
        if (user.getEmail() == null) {
            throw new ValidationException("Электронная почта должна быть указана.");
        }

        log.trace("Обработка имени нового пользователя");
        if ((user.getName() == null) || (user.getName().isBlank())) {
            user.setName(user.getLogin());
            log.trace("Пользователю присвоено имя из логина.");
        }

        log.trace("Проверка даты рождения нового пользователя");
        if (user.getBirthday() == null) {
            throw new ValidationException("Дата рождения должна быть указана.");
        }

        log.trace("Сохранение нового пользователя");
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.info("Обновление данных о пользователе:");

        log.trace("Проверка id пользователя");
        if (user.getId() == null) {
            throw new ValidationException("Id должен быть указан.");
        } else if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с id = " + user.getId() + " не найден.");
        }

        userValidate(user);

        log.trace("Получение текущих данных о пользователе");
        User oldUser = users.get(user.getId());

        log.trace("Обработка логина");
        if (user.getLogin() == null) {
            user.setLogin(oldUser.getLogin());
        } else {
            log.info("Логин пользователя изменен.");
        }

        log.trace("Обработка имени пользователя");
        if (user.getName() == null) {
            user.setName(oldUser.getName());
        } else if ((user.getName().isBlank())) {
            user.setName(user.getLogin());
        } else {
            log.info("Имя пользователя изменено.");
        }

        log.trace("Обработка электронной почты");
        if (user.getEmail() == null) {
            user.setEmail(oldUser.getEmail());
        } else {
            log.info("Электронная почта изменена.");
        }

        log.trace("Обработка даты рождения");
        if (user.getBirthday() == null) {
            user.setBirthday(oldUser.getBirthday());
        } else {
            log.info("Дата рождения изменена.");
        }

        log.trace("Сохранение новых данных пользователя");
        users.put(user.getId(), user);
        return user;
    }

    private void userValidate(User user) {
        log.info("Проверка пользователя:");

        log.trace("Проверка электронной почты");
        if ((user.getEmail() != null) && (user.getEmail().isBlank())) {
            throw new ValidationException("Электронная почта не может быть пустой.");
        } else if ((user.getEmail() != null) && !(user.getEmail().contains("@"))) {
            throw new ValidationException("Электронная почта должна содержать символ '@'.");
        } else if ((user.getEmail() != null) &&
                users.values().stream().map(User::getEmail).anyMatch(u -> u.equals(user.getEmail()))
        ) {
            throw new ValidationException("Эта электронная почта уже используется.");
        }

        log.trace("Проверка логина");
        if ((user.getLogin() != null) && (user.getLogin().isBlank())) {
            throw new ValidationException("Логин не может быть пустым.");
        } else if ((user.getLogin() != null) && (user.getLogin().contains(" "))) {
            throw new ValidationException("Логин не должен содержать пробелов.");
        }

        log.trace("Проверка даты рождения");
        if ((user.getBirthday() != null) && (user.getBirthday().isAfter(LocalDate.now()))) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
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
