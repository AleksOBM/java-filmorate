package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.request.create.FilmCreateRequest;
import ru.yandex.practicum.filmorate.dto.request.update.FilmUpdateRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.util.FilmSortingAction;
import ru.yandex.practicum.filmorate.service.util.LikeAction;

import java.util.*;

@Service
@Slf4j
public class FilmService {

	private final FilmStorage filmStorage;
	private final UserStorage userStorage;
	private final MpaStorage mpaStorage;
	private final GenreStorage genreStorage;
	private final DirectorStorage directorStorage;

	private Map<Integer, Mpa> mpas;
	private Map<Integer, Genre> genres;
	private final EventService eventService;

	@Autowired
	public FilmService(
			@Qualifier("filmDbStorage") FilmStorage filmStorage,
			@Qualifier("userDbStorage") UserStorage userStorage,
			@Qualifier("mpaDbStorage") MpaStorage mpaStorage,
			@Qualifier("genreDbStorage") GenreStorage genreStorage,
			@Qualifier("directorDbStorage") DirectorStorage directorStorage,
			EventService eventService
	) {
		this.filmStorage = filmStorage;
		this.userStorage = userStorage;
		this.mpaStorage = mpaStorage;
		this.genreStorage = genreStorage;
		this.directorStorage = directorStorage;
		this.eventService = eventService;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initMpasAndGenres() {
		this.mpas = mpaStorage.getMapOfAllMpa();
		this.genres = genreStorage.getMapOfAllGenres();
	}

	public Collection<FilmDto> getSortedFilmsOfDirector(Integer directorId, String sortBy) {
		log.info("Получение фильмов по режиссеру");
		if (directorId <= 0) {
			throw new ParameterNotValidException(directorId.toString(), "должен быть положительным числом.");
		}

		Collection<Film> films;

		if (sortBy == null) {
			return getSortedFilmDtosOfDirector(directorId).stream()
					.map(film -> FilmMapper.mapToFilmDto(
									film,
									mpas.get(film.getMpaId()),
									getGenresByIds(film.getGenreIds()),
									getDirectorsByIds(film.getDirectorIds())
							)
					).toList();
		}

		log.trace("Проверка сортировочного запроса");
		FilmSortingAction sortingAction = FilmSortingAction.of(sortBy);

		log.trace("Применение сортировки");
		Comparator<Film> comparator = switch (sortingAction) {
			case YEAR -> Comparator.comparing(Film::getReleaseDate);
			case LIKES -> Comparator.comparing(Film::getLikesCount).reversed();
			case UNDEFINED -> throw new ParameterNotValidException(
					sortingAction.getSortingName(),
					"не подходит. Допустимые значения: " +
							FilmSortingAction.getValidSortingList()
			);
		};

		films = getSortedFilmDtosOfDirector(directorId).stream()
				.sorted(comparator).toList();

		return films.stream()
				.map(film -> FilmMapper.mapToFilmDto(
								film,
								mpas.get(film.getMpaId()),
								getGenresByIds(film.getGenreIds()),
								getDirectorsByIds(film.getDirectorIds())
						)
				).toList();
	}

	private Collection<Film> getSortedFilmDtosOfDirector(Integer directorId) {
		Collection<Film> films = filmStorage.getALLFilmsOfDirector(directorId);

		if (films.isEmpty()) {
			throw new NotFoundException("Фильмы с этим режиссером" +
					" найдены, попробуйте изменить режиссера или параметры сортировки");
		}

		return films;
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
			case SET -> {
				filmStorage.setLike(filmId, userId);
				eventService.addUserEvent(userId, EventType.LIKE, Operation.ADD, filmId);
			}
			case REMOVE -> {
				filmStorage.removeLike(filmId, userId);
				eventService.addUserEvent(userId, EventType.LIKE, Operation.REMOVE, filmId);
			}
		}
	}

	public Collection<FilmDto> getTopFilms(Integer count, Integer genreId, String year) {
		log.info("Получение топ {} фильмов.", count);
		if (count <= 0) {
			throw new ParameterNotValidException(
					String.valueOf(count),
					"Топ фильмов должен быть положительным числом."
			);
		}

		Collection<Film> films;
		if (genreId != null || year != null) {
			films = filmStorage.getTopByFilters(count, genreId, year);
		} else {
			films = filmStorage.getTop(count);
		}

		return films.stream()
				.map(film -> FilmMapper.mapToFilmDto(
								film,
								mpas.get(film.getMpaId()),
								getGenresByIds(film.getGenreIds()),
								getDirectorsByIds(film.getDirectorIds())
						)
				).sorted(Comparator.comparing(FilmDto::getLikesCount).reversed()).toList();
	}

	public Collection<FilmDto> findAll() {
		log.info("Поиск всех фильмов");
		return filmStorage.findAll().stream()
				.map(film -> FilmMapper.mapToFilmDto(
								film,
								mpas.get(film.getMpaId()),
								getGenresByIds(film.getGenreIds()),
								getDirectorsByIds(film.getDirectorIds())
						)
				).toList();
	}

	public FilmDto findById(Long id) {
		log.info("Получение фильма с id={}", id);
		return filmStorage.findById(id)
				.map(film -> FilmMapper.mapToFilmDto(
								film,
								mpas.get(film.getMpaId()),
								getGenresByIds(film.getGenreIds()),
								getDirectorsByIds(film.getDirectorIds())
						)
				)
				.orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден.")
				);
	}

	public Collection<FilmDto> getCommonLikedFilms(Long userId, Long friendId) {
		log.info("Получение общих с другом фильмов userId={}, friendId={}", userId, friendId);
		if (Objects.equals(userId, friendId)) {
			throw new ValidationException("У пользователей должны быть разные id.");
		}
		if (userStorage.checkUserIsNotPresent(userId)) {
			throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
		}
		if (userStorage.checkUserIsNotPresent(friendId)) {
			throw new NotFoundException("Пользователь с id=" + friendId + " не найден.");
		}
		return filmStorage.getCommonLikedFilms(userId, friendId).stream()
				.map(film -> FilmMapper.mapToFilmDto(
								film,
								mpas.get(film.getMpaId()),
								getGenresByIds(film.getGenreIds()),
								getDirectorsByIds(film.getDirectorIds())
						)
				).toList();
	}

	public FilmDto create(FilmCreateRequest request) {
		log.info("Добавление нового фильма");

		Film film = FilmMapper.mapToFilm(request);
		Mpa mpa = validateMpa(film);
		Set<Genre> filmGenres = validateGenres(film);
		Set<Director> filmDirectors = getDirectorsByIds(film.getDirectorIds());

		film = filmStorage.createFilm(film);

		return FilmMapper.mapToFilmDto(film, mpa, filmGenres, filmDirectors);
	}

	public FilmDto update(FilmUpdateRequest request) {
		log.info("Обновление данных о фильме с id={}", request.getId());

		Film oldFilm = filmStorage.findById(request.getId())
				.orElseThrow(() -> new NotFoundException("Фильм с id=" + request.getId() + " не найден."));

		logFilmUpdate(request, oldFilm);

		Film newFilm = FilmMapper.updateFilmFields(oldFilm, request);
		Mpa mpa = validateMpa(newFilm);
		Set<Genre> filmGenres = validateGenres(newFilm);
		Set<Director> filmDirectors = getDirectorsByIds(newFilm.getDirectorIds());
		newFilm = filmStorage.updateFilm(newFilm);

		return FilmMapper.mapToFilmDto(newFilm, mpa, filmGenres, filmDirectors);
	}

	public void remove(long filmId) {
		log.info("Удаление фильма с id={}", filmId);
		if (filmStorage.checkFilmIsNotPresent(filmId)) {
			throw new NotFoundException("Фильм с id=" + filmId + " не найден.");
		}
		filmStorage.removeFilm(filmId);
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

	private Set<Director> getDirectorsByIds(Set<Integer> directorIds) {
		Map<Integer, Director> allDirectorIds = new HashMap<>();
		directorStorage.findAll()
				.forEach(director -> allDirectorIds.put(director.getId(), director));

		log.trace("Проверка режиссеров");
		List<Integer> badDirectorIds = directorIds.stream()
				.filter(id -> !allDirectorIds.containsKey(id))
				.toList();
		if (!badDirectorIds.isEmpty()) {
			throw new NotFoundException("Режиссеры с id " + badDirectorIds + " не найдены.");
		}

		HashSet<Director> dirs = new HashSet<>();
		for (Integer id : directorIds) {
			if (allDirectorIds.containsKey(id)) {
				dirs.add(allDirectorIds.get(id));
			}
		}
		return dirs;
	}

	private void logFilmUpdate(FilmUpdateRequest request, Film film) {
		List<String> updatedFields = new ArrayList<>();

		if (request.hasName() && !film.getName().equals(request.getName())) {
			updatedFields.add("название");
		}
		if (request.hasDescription() && !film.getDescription().equals(request.getDescription())) {
			updatedFields.add("описание");
		}
		if (request.hasReleaseDate() && !film.getReleaseDate().equals(request.getReleaseDate())) {
			updatedFields.add("дата релиза");
		}
		if (request.hasDuration() && !film.getDuration().minus(request.getDuration()).isZero()) {
			updatedFields.add("продолжительность");
		}
		if (request.hasMpa() && !film.getMpaId().equals(request.getMpa().getId())) {
			updatedFields.add("рейтинг MPAA");
		}
		if (request.hasGenres() && !request.getGenres().stream()
				.map(Genre::getId).allMatch(film.getGenreIds()::contains)
		) {
			updatedFields.add("жанры");
		}
		if (request.hasDirectors() && !request.getDirectors().stream()
				.map(Director::getId).allMatch(film.getDirectorIds()::contains)
		) {
			updatedFields.add("режиссеры");
		}

		String updatedData = String.join(", ", updatedFields);
		log.info("Данные для обновления: [{}]", updatedData);
	}

	public Collection<FilmDto> search(String query, String by) {
		log.info("Запрос на поиск фильмов: query='{}', by='{}'", query, by);
		if (query == null || query.isBlank()) {
			throw new ParameterNotValidException("query", "Поисковая строка не может быть пустой.");
		}
		if (by == null || by.isBlank()) {
			throw new ParameterNotValidException("by",
					"Параметр поля для поиска должен содержать 'title' или 'director'");
		}

		return filmStorage.search(query, by).stream()
				.map(film -> FilmMapper.mapToFilmDto(
						film,
						mpas.get(film.getMpaId()),
						getGenresByIds(film.getGenreIds()),
						getDirectorsByIds(film.getDirectorIds())
				))
				.toList();
	}

	public Collection<FilmDto> getFilmsByIds(Collection<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}
		return filmStorage.getFilmsByIds(ids).stream()
				.map(film -> FilmMapper.mapToFilmDto(
						film,
						mpas.get(film.getMpaId()),
						getGenresByIds(film.getGenreIds()),
						getDirectorsByIds(film.getDirectorIds())
				))
				.toList();
	}
}
