package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;
import java.util.Map;
import java.util.Set;

public interface FilmStorage {

	/// Получить фильма по id
	Optional<Film> findById(long id);

	/// Получить всех фильмов
	Collection<Film> findAll();

	/// Добавить нового фильма
	Film createFilm(Film film);

	/// Обновить данные фильма
	Film updateFilm(Film film);

	/// Проверить существование фильма в базе
	boolean checkFilmIsNotPresent(Long filmId);

	/// Получить топ фильмов
	Collection<Film> getTop(int count);

	/// Поставить лайк
	void setLike(long filmId, long userId);

	/// Убрать лайк
	void removeLike(long filmId, long userId);

	// Метод для получения матрицы лайков всех пользователей
	Map<Long, Set<Long>> getAllLikes();
}
