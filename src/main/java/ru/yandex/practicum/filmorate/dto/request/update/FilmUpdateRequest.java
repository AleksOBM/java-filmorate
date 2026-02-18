package ru.yandex.practicum.filmorate.dto.request.update;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.time.DurationMin;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

@Data
public class FilmUpdateRequest {

	@NotNull(message = "Id должен быть указан.")
	@Positive(message = "Id должен быть положительным числом.")
	private Long id;

	@Size(min = 2, max = 100, message = "Название должно содержать от 2 до 100 символов.")
	private String name;

	@Size(max = 200, message = "Максимальная длина описания — 200 символов.")
	private String description;

	@Past(message = "Дата релиза должна быть в прошлом.")
	private LocalDate releaseDate;

	@DurationMin(minutes = 1, message = "Продолжительность фильма должна быть положительным числом.")
	private Duration duration;

	@Size(min = 1, message = "Должен быть указан хотябы один жанр.")
	private Set<Genre> genres;

	@Size(min = 1, message = "Должен быть указан хотябы один режиссер.")
	private Set<Director> directors;

	private Mpa mpa;

	public boolean hasName() {
		return name != null;
	}

	public boolean hasDescription() {
		return description != null;
	}

	public boolean hasReleaseDate() {
		return releaseDate != null;
	}

	public boolean hasDuration() {
		return duration != null;
	}

	public boolean hasGenres() {
		return genres != null;
	}

	public boolean hasDirectors() {
		return directors != null;
	}

	public boolean hasMpa() {
		return mpa != null;
	}
}
