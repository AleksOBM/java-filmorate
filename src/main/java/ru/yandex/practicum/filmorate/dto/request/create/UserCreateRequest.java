package ru.yandex.practicum.filmorate.dto.request.create;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserCreateRequest {

    @Size(max = 50, message = "Длина имени пользователя не может быть больше 50 символов.")
    private String name;

    @NotNull(message = "Логин должен быть указан.")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Логин может состоять только из этих символов [A-Za-z0-9_].")
    @Size(min = 3, max = 30, message = "Длина логина может быть от 3 до 20 символов.")
    private String login;

    @NotNull(message = "Электронная почта должна быть указана.")
    @Email(message = "Не распознан формат адреса электронной почты.")
    private String email;

    @NotNull(message = "Дата рождения должна быть указана.")
    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;

}
