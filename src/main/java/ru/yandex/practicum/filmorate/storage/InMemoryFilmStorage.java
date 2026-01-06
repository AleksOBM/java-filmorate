package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.servise.IdentifyService;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

	private final Map<Long, Film> films;
	private final IdentifyService identifyService;

	@Override
	public Optional<Film> get(Long id) {
		return films.values().stream()
				.filter(f -> f.getId().equals(id)).findFirst();
	}

	@Override
	public Collection<Film> findAll() {
		return films.values();
	}

	@Override
	public Film create(Film film) {
		film.setId(identifyService.getNextId(films));
		films.put(film.getId(), film);
		return film;
	}

	@Override
	public void update(Film film) {
		films.put(film.getId(), film);
	}

	@Override
	public Collection<Long> getFilmIds() {
		return films.keySet();
	}

	@Override
	public void reset() {
		films.clear();
	}
}
