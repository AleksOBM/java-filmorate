package ru.yandex.practicum.filmorate.servise;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;

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

		if (count == null || count <= 0) {
			throw new ParameterNotValidException(count == null ?
					"null" : count.toString(),
					"Топ фильмов должен быть положительным числом."
			);
		}

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
		return filmStorage.get(id).orElseThrow(() ->
						new NotFoundException("Film with id " + id + " not found")
				);
	}

	public Film create(Film film) {
		log.info("Добавление нового фильма:");

		log.trace("Проверка названия нового фильма");
		if (film.getName() == null) {
			throw new ValidationException("Название должно быть указано.");
		}

		log.trace("Проверка даты релиза нового фильма");
		if (film.getReleaseDate() == null) {
			throw new ValidationException("Дата релиза должна быть указана.");
		}

		log.trace("Проверка продолжительности нового фильма");
		if (film.getDuration() == null) {
			throw new ValidationException("Продолжительность фильма должна быть указана.");
		}

		filmValidate(film);

		log.trace("Сохранение нового фильма");
		return filmStorage.create(film);
	}

	public Film update(Film film) {
		log.info("Обновление данных о фильме:");

		if (film == null) {
			throw new ValidationException("Входные данные фильма не распознаны.");
		}

		log.trace("Проверка id фильма");
		if (film.getId() == null) {
			throw new ConditionsNotMetException("Id должен быть указан.");
		} else if (!filmStorage.getFilmIds().contains(film.getId())) {
			throw new NotFoundException("Фильм с id = " + film.getId() + " не найден.");
		}
		filmValidate(film);

		log.trace("Получение текущих данных о фильме");
		Film oldFilm = filmStorage.get(film.getId()).orElseThrow();

		log.trace("Обработка названия фильма");
		if (film.getName() == null) {
			film.setName(oldFilm.getName());
		} else {
			log.info("Название фильма изменено.");
		}

		log.trace("Обработка описания фильма");
		if (film.getDescription() == null) {
			film.setDescription(oldFilm.getDescription());
		} else {
			log.info("Описание фильма изменено.");
		}

		log.trace("Обработка даты релиза фильма");
		if ((film.getReleaseDate() == null)) {
			film.setReleaseDate(oldFilm.getReleaseDate());
		} else {
			log.info("Дата релиза изменена.");
		}

		log.trace("Обработка продолжительности фильма");
		if (film.getDuration() == null) {
			film.setDuration(oldFilm.getDuration());
		} else {
			log.info("Продолжительность фильма изменена.");
		}

		log.trace("Сохранение новых данных о фильме");
		filmStorage.update(film);

		return film;
	}

	private void filmValidate(Film film) {

		log.trace("Проверка названия фильма");
		if (film.getName() != null && film.getName().isBlank()) {
			throw new ValidationException("Название не может быть пустым.");
		}

		log.trace("Проверка описания фильма");
		if (film.getDescription() != null && film.getDescription().length() > 200) {
			throw new ValidationException("Максимальная длина описания — 200 символов.");
		}

		log.trace("Проверка даты релиза фильма");
		if (film.getReleaseDate() != null) {
			if (film.getReleaseDate().isBefore(birthdayOfCinema)) {
				throw new ParameterNotValidException(film.getReleaseDate().toString(),
						"Дата релиза должна быть не раньше 28 декабря 1895 года."
				);
			} else if (film.getReleaseDate().isAfter(LocalDate.now())) {
				throw new ParameterNotValidException(film.getReleaseDate().toString(),
						"Дата релиза не должна быть в будущем."
				);
			}
		}

		log.trace("Проверка продолжительности фильма");
		if (film.getDuration() != null && film.getDuration().toMinutes() <= 0) {
			long minutes = film.getDuration().toMinutes();
			throw new ParameterNotValidException(Long.toString(minutes),
					"Продолжительность фильма должна быть положительным числом."
			);
		}

		log.trace("Проверка дубликатов фильмов");
		if (
				filmStorage.findAll().stream().anyMatch(f ->
						f.getName().equals(film.getName()) &&
								f.getReleaseDate().equals(film.getReleaseDate()))
		) {
			throw new DuplicatedDataException("Фильм с такими названием и датой уже был добавлен ранее");
		}
	}
}
