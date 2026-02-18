package ru.yandex.practicum.filmorate.dto.request.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewCreateRequest {
    @NotBlank(message = "Контент отзыва не может быть пустым.")
    @Size(max = 2048, message = "Максимальная длина отзыва — 2048 символов.")
    private String content;

    @NotNull(message = "Тип отзыва должен быть указан.")
    private Boolean isPositive;

    @NotNull(message = "userId должен быть указан.")
    private Long userId;

    @NotNull(message = "filmId должен быть указан.")
    private Long filmId;
}
