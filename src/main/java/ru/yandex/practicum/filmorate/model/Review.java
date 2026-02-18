package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder(toBuilder = true)
@Jacksonized
@EqualsAndHashCode(of = "id")
public class Review {

    private Long id;
    private String content;
    private Boolean isPositive;
    private Long userId;
    private Long filmId;

    @Builder.Default
    private Long useful = 0L;

    @Builder.Default
    private Map<Long, Boolean> userLikes = new HashMap<>();
}
