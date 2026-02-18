package ru.yandex.practicum.filmorate.dal.inmemory;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.FilmStorage;
import ru.yandex.practicum.filmorate.exception.MethodNotImplementedException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.servise.util.IdentifyService;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

import java.util.Set;

@Component
@Qualifier("inMemoryFilmStorage")
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

	private final Map<Long, Film> films;
	private final IdentifyService identifyService;

	@Override
	public Optional<Film> findById(long id) {
		return films.values().stream()
				.filter(f -> f.getId().equals(id)).findFirst();
	}

	@Override
	public Collection<Film> findAll() {
		return films.values();
	}

	@Override
	public Film createFilm(Film film) {
		film.setId(identifyService.getNextId(films));
		films.put(film.getId(), film);
		return film;
	}

	@Override
	public Film updateFilm(Film film) {
		films.put(film.getId(), film);
		return film;
	}

	@Override
	public boolean checkFilmIsNotPresent(Long filmId) {
		return films.containsKey(filmId);
	}

	@Override
	public Collection<Film> getTop(int count) {
		throw new MethodNotImplementedException();
	}

	@Override
	public void removeLike(long filmId, long userId) {
		throw new MethodNotImplementedException();
	}

	@Override
	public void setLike(long filmId, long userId) {
		throw new MethodNotImplementedException();
	}

	@Override
	public Map<Long, Set<Long>> getAllLikes() {
		return new HashMap<>();
	}
}
