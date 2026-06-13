package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Like {
	Long id;
	long filmId;
	long userId;
	Assessment assessment;
}
