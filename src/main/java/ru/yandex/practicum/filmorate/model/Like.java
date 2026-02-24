package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Like {
	long id;
	long filmId;
	long userId;
	Assessment assessment;
}
