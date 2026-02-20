package ru.yandex.practicum.filmorate.dto.request.create;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.time.DurationMin;
import ru.yandex.practicum.filmorate.dto.adapters.MinDate;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class FilmCreateRequest {

	@Size(min = 2, max = 100, message = "Название должно содержать от 2 до 100 символов.")
	@NotNull(message = "Название должно быть указано.")
	private String name;

	@Size(max = 200, message = "Максимальная длина описания — 200 символов.")
	@NotNull(message = "Описание должно быть указано.")
	private String description;

	@Past(message = "Дата релиза должна быть в прошлом.")
	@NotNull(message = "Дата релиза должна быть указана.")
	@MinDate(minDate = "1895-12-28")
	private LocalDate releaseDate;

	@NotNull(message = "Продолжительность фильма должна быть указана.")
	@DurationMin(seconds = 1, message = "Продолжительность фильма должна быть положительным числом.")
	private Duration duration;

	@Valid
	@NotNull(message = "Рейтинг MPAA должен быть указан.")
	private Mpa mpa;

	private Set<Genre> genres = new HashSet<>();

	private Set<Director> directors = new HashSet<>();

}
