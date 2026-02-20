package ru.yandex.practicum.filmorate.dto.request.update;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateRequest {

    @NotNull(message = "Id должен быть указан.")
    @Positive(message = "Id должен быть положительным числом.")
    private Long id;

    @Size(max = 50, message = "Длина имени пользователя не может быть больше 50 символов.")
    private String name;

    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Логин может состоять только из этих символов [A-Za-z0-9_].")
    @Size(min = 3, max = 30, message = "Длина логина может быть от 3 до 20 символов.")
    private String login;

    @Email(message = "Не распознан формат адреса электронной почты.")
    private String email;

    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;

    public boolean hasNname() {
        return name != null;
    }

    public boolean hasLogin() {
        return login != null;
    }

    public boolean hasEmail() {
        return email != null;
    }

    public boolean hasBirthday() {
        return birthday != null;
    }
}
