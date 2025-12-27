package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

	/// Получить фильма по id
	Optional<Film> get(Long id);

	/// Получить всех фильмов
	Collection<Film> findAll();

	/// Добавить нового фильма
	Film create(Film film);

	/// Обновить данные фильма
	void update(Film film);

	/// Получение id всех Фильмов
	Collection<Long> getFilmIds();

	void reset();
}
