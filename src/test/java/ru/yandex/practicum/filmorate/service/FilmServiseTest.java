package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.AgeRating;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.servise.FilmService;
import ru.yandex.practicum.filmorate.servise.IdentifyService;
import ru.yandex.practicum.filmorate.servise.LikeAction;
import ru.yandex.practicum.filmorate.servise.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmServiseTest {

	private UserService userService;
	private FilmService filmService;

	@BeforeEach
	void beforeEach() {
		UserStorage userStorage = new InMemoryUserStorage(new HashMap<>(), new IdentifyService());
		userService = new UserService(userStorage);
		filmService = new FilmService(
				new InMemoryFilmStorage(new HashMap<>(), new IdentifyService()),
				userStorage
		);

		userService.create(User.builder()
				.login("User1")
				.email("user1@main.com")
				.birthday(LocalDate.of(2003, 10, 2))
				.build());

		filmService.create(Film.builder()
				.name("Film1")
				.description("this is description for film1")
				.releaseDate(LocalDate.of(2018, 1, 11))
				.duration(Duration.ofMinutes(135))
				.genres(Set.of(Genre.COMEDY))
				.ageRating(AgeRating.G)
				.build());
	}

	@Nested
	@DisplayName("Добавление фильма.")
	class Create {

		@Test
		void throwsWhenFilmWithoutReleaseDate() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> filmService.create(Film.builder()
							.name("XXX")
							.genres(Set.of(Genre.COMEDY))
							.ageRating(AgeRating.G)
							.build()
					));
			assertEquals("Дата релиза должна быть указана.", exception.getMessage());
		}

		@Test
		void throwsWhenFilmWithTooEarlyReleaseDate() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> filmService.create(Film.builder()
							.name("XXX")
							.releaseDate(LocalDate.of(1895, 12, 27))
							.duration(Duration.ofMinutes(25))
							.genres(Set.of(Genre.COMEDY))
							.ageRating(AgeRating.G)
							.build()
					));
			assertEquals("Дата релиза должна быть не раньше 28 декабря 1895 года.", exception.getMessage());
		}

		@Test
		void throwsWhenFilmWithEmptyDuration() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> filmService.create(Film.builder()
							.name("XXX")
							.releaseDate(LocalDate.of(2021, 12, 27))
							.genres(Set.of(Genre.COMEDY))
							.ageRating(AgeRating.G)
							.build()
					));
			assertEquals("Продолжительность фильма должна быть указана.", exception.getMessage());
		}

		@Test
		void throwsWhenFilmWithNullableFilm() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> filmService.update(null));
			assertEquals("Входные данные фильма не распознаны.", exception.getMessage());
		}

		@Test
		void throwsWhenFilmWithoutName() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> filmService.create(Film.builder()
							.releaseDate(LocalDate.of(2021, 12, 27))
							.duration(Duration.ofMinutes(10))
							.genres(Set.of(Genre.COMEDY))
							.ageRating(AgeRating.G)
							.build()
					));
			assertEquals("Название должно быть указано.", exception.getMessage());
		}

		void throwsWhenFilmWithEmptyName() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> filmService.create(Film.builder()
							.name("")
							.releaseDate(LocalDate.of(2010, 1, 2))
							.duration(Duration.ofMinutes(145))
							.genres(Set.of(Genre.COMEDY))
							.ageRating(AgeRating.G)
							.build()
					));
			assertEquals("Название не может быть пустым.", exception.getMessage());
		}

		void throwsWhenFilmWithTooLongDescription() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> filmService.create(Film.builder()
							.name("XXX")
							.description("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
									"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
									"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
									"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
							.releaseDate(LocalDate.of(2010, 1, 2))
							.duration(Duration.ofMinutes(423))
							.genres(Set.of(Genre.COMEDY))
							.ageRating(AgeRating.G)
							.build()
					));
			assertEquals("Максимальная длина описания — 200 символов.", exception.getMessage());
		}

		void throwsWhenFilmWithReleaseDateAtFuture() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> filmService.create(Film.builder()
							.name("XXX")
							.releaseDate(LocalDate.of(3895, 12, 27))
							.duration(Duration.ofMinutes(90))
							.genres(Set.of(Genre.COMEDY))
							.ageRating(AgeRating.G)
							.build()
					));
			assertEquals("Дата релиза не должна быть в будущем.", exception.getMessage());
		}

		void throwsWhenFilmWithNegativeDuration() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> filmService.create(Film.builder()
							.name("XXX")
							.releaseDate(LocalDate.of(2021, 12, 27))
							.duration(Duration.ofMinutes(-1))
							.genres(Set.of(Genre.COMEDY))
							.ageRating(AgeRating.G)
							.build()
					));
			assertEquals("Продолжительность фильма должна быть положительным числом.", exception.getMessage());
		}

		void throwsWhenFilmWithNullableDuration() {
			RuntimeException exception1 = assertThrows(RuntimeException.class,
					() -> filmService.create(Film.builder()
							.name("XXX")
							.releaseDate(LocalDate.of(2021, 12, 27))
							.duration(Duration.ofMinutes(0))
							.genres(Set.of(Genre.COMEDY))
							.ageRating(AgeRating.G)
							.build()
					));
			assertEquals("Продолжительность фильма должна быть положительным числом.", exception1.getMessage());
		}
	}

	@Nested
	@DisplayName("Обновление фильма.")
	class Update {

		@Test
		void saveActualFilmAfterUpdate() {
			filmService.update(Film.builder()
					.id(1L)
					.name("UpdatedName")
					.description("this is updated description")
					.releaseDate(LocalDate.of(2019, 11, 21))
					.duration(Duration.ofMinutes(37))
					.build());
			Film film = filmService.findAll().stream().findFirst().orElse(Film.builder().build());

			Assertions.assertAll(
					() -> assertEquals("UpdatedName", film.getName()),
					() -> assertEquals("this is updated description", film.getDescription()),
					() -> assertEquals("2019-11-21", film.getReleaseDate().toString()),
					() -> assertEquals(Duration.ofMinutes(37), film.getDuration())
			);
		}

		@Test
		void saveOldFilmFieldsWhenFilmWithoutThisFields() {
			filmService.update(Film.builder()
					.id(1L)
					.build());
			Film film = filmService.findAll().stream().findFirst().orElse(Film.builder().build());

			Assertions.assertAll(
					() -> assertEquals("Film1", film.getName()),
					() -> assertEquals("this is description for film1", film.getDescription()),
					() -> assertEquals("2018-01-11", film.getReleaseDate().toString()),
					() -> assertEquals(Duration.parse("PT2H15M"), film.getDuration())
			);
		}

		@Test
		void shouldReturnExceptionAfterTryUpdateFilmWithoutId() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> filmService.update(Film.builder()
							.name("XXX")
							.releaseDate(LocalDate.of(2021, 12, 27))
							.build()
					));
			assertEquals("Id должен быть указан.", exception.getMessage());
		}

		@Test
		void throwsWhenFilmWithLostId() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> filmService.update(Film.builder()
							.id(777L)
							.name("XXX")
							.releaseDate(LocalDate.of(2021, 12, 27))
							.build()
					));
			assertEquals("Фильм с id = 777 не найден.", exception.getMessage());
		}

		@Test
		void throwsWhenFilmWithTooEarlyReleaseDate() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> filmService.update(Film.builder()
							.id(1L)
							.releaseDate(LocalDate.of(1895, 1, 27))
							.build()
					));
			assertEquals("Дата релиза должна быть не раньше 28 декабря 1895 года.", exception.getMessage());
		}

		void throwsWhenFilmWithEmptyFilmName() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> filmService.update(Film.builder()
							.id(1L)
							.name("")
							.releaseDate(LocalDate.of(2021, 12, 27))
							.build()
					));
			assertEquals("Название не может быть пустым.", exception.getMessage());
		}

		void throwsWhenFilmWithTooLongDescription() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> filmService.update(Film.builder()
							.id(1L)
							.description("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
									"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
									"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
									"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
							.releaseDate(LocalDate.of(2021, 12, 27))
							.build()
					));
			assertEquals("Максимальная длина описания — 200 символов.", exception.getMessage());
		}

		void throwsWhenFilmWithReleaseDateAtFuture() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> filmService.update(Film.builder()
							.id(1L)
							.releaseDate(LocalDate.of(3895, 1, 27))
							.build()
					));
			assertEquals("Дата релиза не должна быть в будущем.", exception.getMessage());
		}

		void throwsWhenFilmWithNegativeDuration() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> filmService.update(Film.builder()
							.id(1L)
							.duration(Duration.ofMinutes(-100))
							.build()
					));
			assertEquals("Продолжительность фильма должна быть положительным числом.", exception.getMessage());
		}

		void throwsWhenFilmWithNullableDuration() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> filmService.update(Film.builder()
							.id(1L)
							.duration(Duration.ofMinutes(0))
							.build()
					));
			assertEquals("Продолжительность фильма должна быть положительным числом.", exception.getMessage());
		}
	}

	@Nested
	@DisplayName("Получение всех фильмов.")
	class Get {
		@Test
		void returnsActualFilmWhenGetAll() {
			filmService.create(Film.builder()
					.name("Film2")
					.description("this is description for film2")
					.releaseDate(LocalDate.of(2020, 11, 21))
					.duration(Duration.ofMinutes(35))
					.genres(Set.of(Genre.COMEDY))
					.ageRating(AgeRating.G)
					.build());

			Collection<Film> allFilms = filmService.findAll();

			assertEquals(2, allFilms.size());

			Film film = allFilms.stream().filter(f -> f.getId() == 1)
					.findFirst().orElse(Film.builder().build());

			Assertions.assertAll(
					() -> assertEquals("Film1", film.getName()),
					() -> assertEquals("this is description for film1", film.getDescription()),
					() -> assertEquals("2018-01-11", film.getReleaseDate().toString()),
					() -> assertEquals(Duration.ofMinutes(135), film.getDuration()),
					() -> assertEquals(Genre.COMEDY, film.getGenres().stream().findFirst().orElse(null)),
					() -> assertEquals(AgeRating.G, film.getAgeRating())
			);
		}
	}

	@Nested
	@DisplayName("Лайки и рейтинги фильмов.")
	class Likes {

		@Test
		void setLike() {
			filmService.changeLike(LikeAction.SET,  1L, 1L);
			assertTrue(filmService.get(1L).getWhoLiked().contains(1L));
		}

		@Test
		void removeLike() {
			userService.create(User.builder()
					.login("User2")
					.email("user2@main.com")
					.birthday(LocalDate.of(2003, 10, 2))
					.build());

			filmService.changeLike(LikeAction.SET,  1L, 1L);
			filmService.changeLike(LikeAction.SET,  1L, 2L);

			filmService.changeLike(LikeAction.REMOVE,  1L, 1L);

			assertEquals(1, filmService.get(1L).getWhoLiked().size());
			assertTrue(filmService.get(1L).getWhoLiked().contains(2L));
		}

		@Test
		void topFilms() {
			userService.create(User.builder()
					.login("User2")
					.email("user2@main.com")
					.birthday(LocalDate.of(2003, 10, 2))
					.build());

			userService.create(User.builder()
					.login("User3")
					.email("user3@main.com")
					.birthday(LocalDate.of(2003, 10, 2))
					.build());

			userService.create(User.builder()
					.login("User4")
					.email("user4@main.com")
					.birthday(LocalDate.of(2003, 10, 2))
					.build());

			userService.create(User.builder()
					.login("User5")
					.email("user5@main.com")
					.birthday(LocalDate.of(2003, 10, 2))
					.build());

			Film film2 = filmService.create(Film.builder()
					.name("Film2")
					.description("")
					.releaseDate(LocalDate.of(2019, 1, 11))
					.duration(Duration.ofMinutes(135))
					.genres(Set.of(Genre.COMEDY))
					.ageRating(AgeRating.G)
					.build());

			Film film3 = filmService.create(Film.builder()
					.name("Film3")
					.description("")
					.releaseDate(LocalDate.of(2020, 1, 11))
					.duration(Duration.ofMinutes(135))
					.genres(Set.of(Genre.COMEDY))
					.ageRating(AgeRating.G)
					.build());

			Film film4 = filmService.create(Film.builder()
					.name("Film4")
					.description("")
					.releaseDate(LocalDate.of(2021, 1, 11))
					.duration(Duration.ofMinutes(135))
					.genres(Set.of(Genre.COMEDY))
					.ageRating(AgeRating.G)
					.build());

			Film film5 = filmService.create(Film.builder()
					.name("Film5")
					.description("")
					.releaseDate(LocalDate.of(2022, 1, 11))
					.duration(Duration.ofMinutes(135))
					.genres(Set.of(Genre.COMEDY))
					.ageRating(AgeRating.G)
					.build());

			filmService.changeLike(LikeAction.SET,  3L, 1L);
			filmService.changeLike(LikeAction.SET,  3L, 2L);
			filmService.changeLike(LikeAction.SET,  3L, 3L);
			filmService.changeLike(LikeAction.SET,  3L, 4L);
			filmService.changeLike(LikeAction.SET,  3L, 5L);

			filmService.changeLike(LikeAction.SET,  1L, 1L);
			filmService.changeLike(LikeAction.SET,  1L, 2L);
			filmService.changeLike(LikeAction.SET,  1L, 3L);
			filmService.changeLike(LikeAction.SET,  1L, 4L);

			filmService.changeLike(LikeAction.SET,  4L, 1L);
			filmService.changeLike(LikeAction.SET,  4L, 2L);
			filmService.changeLike(LikeAction.SET,  4L, 3L);

			filmService.changeLike(LikeAction.SET,  2L, 1L);
			filmService.changeLike(LikeAction.SET,  2L, 2L);

			filmService.changeLike(LikeAction.SET,  5L, 1L);

			ArrayList<Film> top = new ArrayList<>(filmService.getTopFilms(5));

			assertEquals(5, top.size());
			assertEquals(top.get(0), film3);
			assertEquals(top.get(1), filmService.get(1L));
			assertEquals(top.get(2), film4);
			assertEquals(top.get(3), film2);
			assertEquals(top.get(4), film5);


		}
	}

}
