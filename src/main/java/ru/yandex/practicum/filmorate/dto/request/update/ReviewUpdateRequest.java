package ru.yandex.practicum.filmorate.dto.request.update;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewUpdateRequest {
    @NotNull(message = "reviewId должен быть указан.")
    private Long reviewId;

    @NotNull(message = "Контент отзыва не может быть пустым.")
    @Size(max = 2048, message = "Максимальная длина отзыва — 2048 символов.")
    private String content;

    @NotNull(message = "Тип отзыва должен быть указан.")
    private Boolean isPositive;
}
