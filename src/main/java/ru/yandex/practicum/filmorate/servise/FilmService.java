package ru.yandex.practicum.filmorate.servise;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.AgeRating;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

	private final LocalDate birthdayOfCinema = LocalDate.of(1895, 12, 28);

	private final FilmStorage filmStorage;
	private final UserStorage userStorage;

	public void changeLike(LikeAction action, Long filmId, Long userId) {
		switch (action) {
			case SET -> log.info("Добавление лайка пользователя id={} к фильму id={}.", userId, filmId);
			case REMOVE -> log.info("Удаление лайка пользователя id={} у фильма id={}.", userId, filmId);
		}

		Film film = filmStorage.get(filmId).orElseThrow(() ->
				new NotFoundException("Фильм с id=" + filmId + " не найден.")
		);
		User user = userStorage.get(userId).orElseThrow(() ->
				new NotFoundException("Пользователь с id=" + userId + " не найден.")
		);

		switch (action) {
			case SET -> film.setLike(user);
			case REMOVE -> film.removeLike(user);
		}
	}

	public Collection<Film> getTopFilms(Integer count) {
		log.info("Получение топ{} фильмов.", count);

		return filmStorage.findAll().stream()
				.sorted(Comparator.comparing(Film::getLikesCount).reversed())
				.limit(count)
				.toList();
	}

	public Collection<Film> findAll() {
		log.info("Поиск всех фильмов");
		return filmStorage.findAll();
	}

	public Film get(Long id) {
		log.info("Получение фильма по id");
		return filmStorage.get(id).orElseThrow(() ->
				new NotFoundException("Фильм с id=" + id + " не найден.")
		);
	}

	public Film create(Film film) {
		log.info("Добавление нового фильма");

		if (film.getName() == null || film.getName().isBlank()) {
			throw new ValidationException("Название должно быть указано.");
		}

		if (film.getReleaseDate() == null) {
			throw new ValidationException("Дата релиза должна быть указана.");
		}

		if (film.getDuration() == null) {
			throw new ValidationException("Продолжительность фильма должна быть указана.");
		}

		if (film.getAgeRating() == null) {
			throw new ValidationException("Возрастной рейтинг фильма должен быть указан.");
		}

		if (film.getGenres() == null || film.getGenres().isEmpty()) {
			throw new ValidationException("Должен быть указан хотя-бы один жанр.");
		}

		filmValidate(film);

		return filmStorage.create(film);
	}

	public Film update(Film film) {
		log.info("Обновление данных о фильме");

		if (film == null) {
			throw new ValidationException("Входные данные фильма не распознаны.");
		}

		Long id = film.getId();
		if (id == null) {
			throw new ConditionsNotMetException("Id должен быть указан.");
		} else if (!filmStorage.getFilmIds().contains(id)) {
			throw new NotFoundException("Фильм с id = " + id + " не найден.");
		}

		filmValidate(film);

		Film oldFilm = filmStorage.get(id).orElseThrow();

		if (film.getName() == null || film.getName().equals(oldFilm.getName())) {
			film.setName(oldFilm.getName());
		} else {
			log.info("Название фильма изменено.");
		}

		if (film.getDescription() == null || film.getDescription().equals(oldFilm.getDescription())) {
			film.setDescription(oldFilm.getDescription());
		} else {
			log.info("Описание фильма изменено.");
		}

		if (film.getReleaseDate() == null || film.getReleaseDate().equals(oldFilm.getReleaseDate())) {
			film.setReleaseDate(oldFilm.getReleaseDate());
		} else {
			log.info("Дата релиза изменена.");
		}

		if (film.getDuration() == null || film.getDuration().equals(oldFilm.getDuration())) {
			film.setDuration(oldFilm.getDuration());
		} else {
			log.info("Продолжительность фильма изменена.");
		}

		if (film.getAgeRating() == null || film.getAgeRating().equals(oldFilm.getAgeRating())) {
			film.setAgeRating(oldFilm.getAgeRating());
		} else {
			log.info("Возрастной рейтинг фильма изменен.");
		}

		Set<Genre> filmGenres = film.getGenres();
		Set<Genre> oldFilmGenres = oldFilm.getGenres();
		if (filmGenres == null || filmGenres.isEmpty() ||
				(filmGenres.containsAll(oldFilmGenres) && oldFilmGenres.containsAll(filmGenres))
		) {
			film.setGenres(oldFilmGenres);
		} else {
			log.info("Жанры фильма изменены.");
		}

		filmStorage.update(film);
		return film;
	}

	private void filmValidate(Film film) {

		LocalDate releaseDate = film.getReleaseDate();
		if (releaseDate != null && releaseDate.isBefore(birthdayOfCinema)) {
			throw new ParameterNotValidException(releaseDate.toString(),
					"Дата релиза должна быть не раньше 28 декабря 1895 года."
			);
		}

		AgeRating ageRating = film.getAgeRating();
		if (ageRating != null && ageRating.equals(AgeRating.UNEXPECTED)) {
			throw new ParameterNotValidException(ageRating.toString(),
					"Возрастной рейтинг может быть только чем-то из этого списка: " +
							AgeRating.getValidAgeRatingList() + "."
			);
		}

		if (film.getGenres() != null && !film.getGenres().isEmpty()) {
			Set<Genre> genres = film.getGenres();
			Genre unexpectedGenre = genres.stream().filter(Genre.UNEXPECTED::equals).findFirst().orElse(null);

			if (unexpectedGenre != null) {
				throw new ParameterNotValidException(unexpectedGenre.toString(),
						"Жанр может быть только чем-то из этого списка: " +
								Genre.getValidGenresList() + "."
				);
			}
		}

		if (filmStorage.findAll().stream()
						.filter(f -> !f.getId().equals(film.getId()))
						.anyMatch(f ->
								f.getName().equals(film.getName()) &&
										f.getReleaseDate().equals(film.getReleaseDate()))
		) {
			throw new DuplicatedDataException("Фильм с такими названием и датой уже был добавлен ранее.");
		}

	}

}
