package ru.yandex.practicum.filmorate.dal.inmemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.FilmStorage;
import ru.yandex.practicum.filmorate.exception.MethodNotImplementedException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class InMemoryFilmStorage implements FilmStorage {

	@Override
	public Optional<Film> findById(long id) {
		throw new MethodNotImplementedException();
	}

	@Override
	public Collection<Film> findAll() {
		throw new MethodNotImplementedException();
	}

	@Override
	public Film createFilm(Film film) {
		throw new MethodNotImplementedException();
	}

	@Override
	public Film updateFilm(Film film) {
		throw new MethodNotImplementedException();
	}

	@Override
	public boolean checkFilmIsNotPresent(Long filmId) {
		throw new MethodNotImplementedException();
	}

	@Override
	public Collection<Film> getTop(int count) {
		throw new MethodNotImplementedException();
	}

	@Override
	public Collection<Film> getTopByFilters(Integer count, Integer genreId, String year) {
		throw new MethodNotImplementedException();
	}

	@Override
	public void setLike(long filmId, long userId) {
		throw new MethodNotImplementedException();
	}

	@Override
	public void removeLike(long filmId, long userId) {
		throw new MethodNotImplementedException();
	}

	@Override
	public Collection<Film> getALLFilmsOfDirector(Integer directorId) {
		throw new MethodNotImplementedException();
	}

	@Override
	public Map<Long, Set<Long>> getAllLikes() {
		throw new MethodNotImplementedException();
	}

	@Override
	public Collection<Film> getCommonLikedFilms(long userId, long friendId) {
		throw new MethodNotImplementedException();
	}

	@Override
	public Collection<Film> search(String query, String by) {
		throw new MethodNotImplementedException();
	}

	@Override
	public void removeFilm(long filmId) {
		throw new MethodNotImplementedException();
	}

	@Override
	public Collection<Film> getFilmsByIds(Collection<Long> ids) {
		throw new MethodNotImplementedException();
	}
}