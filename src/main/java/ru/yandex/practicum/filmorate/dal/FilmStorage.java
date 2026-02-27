package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Assessment;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;
import java.util.Optional;
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

	/// Получить топ фильмов по фильтрам
	Collection<Film> getTopByFilters(Integer count, Integer genreId, String year);

	/// Поставить лайк с оценкой (по умолчанию оценка 10)
	void addLike(long filmId, long userId, Assessment assessment);

	/// Убрать лайк
	void removeLike(long filmId, long userId);

	/// Получить список фильмов по id режиссера
	Collection<Film> getALLFilmsOfDirector(Integer directorId);

	/// Получить все лайки
	Set<Like> getAllLikes();

	/// Получить список филиьов, которые понравились обоим пользователям
	Collection<Film> getCommonLikedFilms(long userId, long friendId);

	/// Поиск фильмов по названию и описанию
	Collection<Film> search(String query, String by);

	/// Удаление фильма по id
	void removeFilm(long filmId);

	/// Получить коллекцию фильмов по id фильмов
	Collection<Film> getFilmsByIds(Collection<Long> ids);
}
