package ru.yandex.practicum.filmorate.servise;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmStorage;
import ru.yandex.practicum.filmorate.dal.GenreStorage;
import ru.yandex.practicum.filmorate.dal.MpaStorage;
import ru.yandex.practicum.filmorate.dal.UserStorage;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.request.create.FilmCreateRequest;
import ru.yandex.practicum.filmorate.dto.request.update.FilmUpdateRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.servise.util.LikeAction;

import java.util.*;

@Service
@Slf4j
public class FilmService {

	private final FilmStorage filmStorage;
	private final UserStorage userStorage;
	private final MpaStorage mpaStorage;
	private final GenreStorage genreStorage;

	private Map<Integer, Mpa> mpas;
	private Map<Integer, Genre> genres;

	@Autowired
	public FilmService(
			@Qualifier("filmDbStorage") FilmStorage filmStorage,
			@Qualifier("userDbStorage") UserStorage userStorage,
			@Qualifier("mpaDbStorage") MpaStorage mpaStorage,
			@Qualifier("genreDbStorage") GenreStorage genreStorage
	) {
		this.filmStorage = filmStorage;
		this.userStorage = userStorage;
		this.mpaStorage = mpaStorage;
		this.genreStorage = genreStorage;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initMpasAndGenres() {
		this.mpas = mpaStorage.getMapOfAllMpa();
		this.genres = genreStorage.getMapOfAllGenres();
	}

	public void changeLike(LikeAction action, Long filmId, Long userId) {
		switch (action) {
			case SET -> log.info("Добавление лайка пользователя id={} к фильму id={}.", userId, filmId);
			case REMOVE -> log.info("Удаление лайка пользователя id={} у фильма id={}.", userId, filmId);
		}

		if (filmStorage.checkFilmIsNotPresent(filmId)) {
			throw new NotFoundException("Фильм с id=" + filmId + " не найден.");
		}

		if (userStorage.checkUserIsNotPresent(userId)) {
			throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
		}

		switch (action) {
			case SET -> filmStorage.setLike(filmId, userId);
			case REMOVE -> filmStorage.removeLike(filmId, userId);
		}
	}

	public Collection<FilmDto> getTopFilms(Integer count) {
		log.info("Получение топ {} фильмов.", count);
		if (count <= 0) {
			throw new ParameterNotValidException(
					String.valueOf(count),
					"Топ фильмов должен быть положительным числом."
			);
		}
		return filmStorage.getTop(count).stream()
				.map(film -> FilmMapper.mapToFilmDto(
								film,
								mpas.get(film.getMpaId()),
								getGenresByIds(film.getGenreIds())
						)
				).sorted(Comparator.comparing(FilmDto::getLikesCount).reversed()).toList();
	}

	public Collection<FilmDto> findAll() {
		log.info("Поиск всех фильмов");
		return filmStorage.findAll().stream()
				.map(film -> FilmMapper.mapToFilmDto(
								film,
								mpas.get(film.getMpaId()),
								getGenresByIds(film.getGenreIds())
						)
				).toList();
	}

	public FilmDto findById(Long id) {
		log.info("Получение фильма с id={}", id);
		return filmStorage.findById(id)
				.map(film -> FilmMapper.mapToFilmDto(
								film,
								mpas.get(film.getMpaId()),
								getGenresByIds(film.getGenreIds())
						)
				)
				.orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден.")
				);
	}

	public FilmDto create(FilmCreateRequest request) {
		log.info("Добавление нового фильма");

		Film film = FilmMapper.mapToFilm(request);
		Mpa mpa = validateMpa(film);
		Set<Genre> filmGenres = validateGenres(film);

		film = filmStorage.createFilm(film);

		return FilmMapper.mapToFilmDto(film, mpa, filmGenres);
	}

	public FilmDto update(FilmUpdateRequest request) {
		log.info("Обновление данных о фильме с id={}", request.getId());

		Film oldFilm = filmStorage.findById(request.getId())
				.orElseThrow(() -> new NotFoundException("Фильм не найден, обновлять нечего."));

		logFilmUpdate(request, oldFilm);

		Film newFilm = FilmMapper.updateFilmFields(oldFilm, request);
		Mpa mpa = validateMpa(newFilm);
		Set<Genre> filmGenres = validateGenres(newFilm);
		newFilm = filmStorage.updateFilm(newFilm);

		return FilmMapper.mapToFilmDto(newFilm, mpa, filmGenres);
	}

	private Mpa validateMpa(Film film) {
		log.trace("Проверка рейтинга MPAA");
		int mpaId = film.getMpaId();
		return mpaStorage.getMpaById(mpaId).orElseThrow(() ->
				new NotFoundException("Рейтинг MPAA с id=" + mpaId + " не найден."));
	}

	private Set<Genre> validateGenres(Film film) {
		log.trace("Проверка жанров");
		Set<Integer> requestGenreIds = film.getGenreIds();
		List<Integer> badGenreids = requestGenreIds.stream()
				.filter(id -> !genres.containsKey(id))
				.sorted().toList();
		if (!badGenreids.isEmpty()) {
			throw new NotFoundException("Жанры с id " + badGenreids + " не найдены.");
		}
		return getGenresByIds(requestGenreIds);
	}

	private Set<Genre> getGenresByIds(Set<Integer> genreIds) {
		HashSet<Genre> gnrs = new HashSet<>();
		for (Integer id : genreIds) {
			if (genres.containsKey(id)) {
				gnrs.add(genres.get(id));
			}
		}
		return gnrs;
	}

	private void logFilmUpdate(FilmUpdateRequest request, Film film) {
		StringBuilder updatedData = new StringBuilder();
		updatedData.append("[");
		if (request.hasNname() && !film.getName().equals(request.getName())) {
			updatedData.append("название").append(", ");
		}
		if (request.hasDescription() && !film.getDescription().equals(request.getDescription())) {
			updatedData.append("описание").append(", ");
		}
		if (request.hasReleaseDate() && !film.getReleaseDate().equals(request.getReleaseDate())) {
			updatedData.append("дата релиза").append(", ");
		}
		if (request.hasDuration() && !film.getDuration().minus(request.getDuration()).isZero()) {
			updatedData.append("продолжительность").append(", ");
		}
		if (request.hasMpa() && !film.getMpaId().equals(request.getMpa().getId())) {
			updatedData.append("рейтинг MPAA").append(", ");
		}
		if (request.hasGenres() && !request.getGenres().stream()
				.map(Genre::getId).allMatch(film.getGenreIds()::contains)
		) {
			updatedData.append("жанры").append(", ");
		}
		if (updatedData.length() > 2) {
			updatedData.setLength(updatedData.length() - 2);
		}
		updatedData.append("]");
		log.info("Данные для обновления: {}", updatedData);
	}

}
