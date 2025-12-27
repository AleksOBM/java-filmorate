package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.annotation.SortedPosition;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.servise.FilmService;
import ru.yandex.practicum.filmorate.servise.IdentifyService;
import ru.yandex.practicum.filmorate.servise.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {

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
				.build());
	}

	@Test
	@DisplayName("Правильное добавление пользователя.")
	void createUserSuccessTest() {
		shouldSaveActualUserAfterCreate();
		shouldSaveUserNameOfNewLoginAfterCreateUserWithEmptyUserName();
	}

	@Test
	@DisplayName("Неправильное добавление пользователя.")
	void createUserFailureTest() {
		shouldReturnExceptionAfterTryCreateUserWithEmptyEmail();
		shouldReturnExceptionAfterTryCreateUserWithNotValidEmail();
		shouldReturnExceptionAfterTryCreateUserWithDuplicatedEmail();
		shouldReturnExceptionAfterTryCreateUserWithEmptyLogin();
		shouldReturnExceptionAfterTryCreateUserWithNotValidLogin();
		shouldReturnExceptionAfterTryCreateUserWithBirthdayAtFuture();
		shouldReturnExceptionAfterTryCreateUserWithoutBirthday();
		shouldReturnExceptionAfterTryCreateUserWithoutLogin();
		shouldReturnExceptionAfterTryCreateUserWithoutEmail();
	}

	@Test
	@DisplayName("Правильное обновление пользователя.")
	void updateUserSuccessTest() {
		shouldSaveActualUserAfterUpdate();
		shouldSaveUserNameOfNewLoginAfterUpdateUserWithEmptyUserName();
		shouldSaveOldUserEqualsNewLoginNameAfterUpdateUserWithoutUserName();
		shouldSaveOldUserFieldsAfterUpdateWithoutThisFields();
	}

	@Test
	@DisplayName("Неправильное обновление пользователя.")
	void updateUserFailureTest() {
		shouldReturnExceptionAfterTryUpdateUserWithoutId();
		shouldReturnExceptionAfterTryUpdateUserWithLostId();
		shouldReturnExceptionAfterTryUpdateUserWithEmptyLogin();
		shouldReturnExceptionAfterTryUpdateUserWithNotValidLogin();
		shouldReturnExceptionAfterTryUpdateUserWithEmptyEmail();
		shouldReturnExceptionAfterTryUpdateUserWithNotValidEmail();
		shouldReturnExceptionAfterTryUpdateUserWithDuplicatedEmail();
	}

	@Test
	@DisplayName("Правильное получение пользователя.")
	void getUserSuccessTest() {
		shouldReturnActualUserAfterGet();
	}

	@Test
	@DisplayName("Правильное добавление фильма.")
	void createFilmSuccessTest() {
		shouldSaveActualFilmAfterCreate();
	}

	@Test
	@DisplayName("Неправильное добавление фильма.")
	void createFilmFailureTest() {
		shouldReturnExceptionAfterTryCreateFilmWithEmptyName();
		shouldReturnExceptionAfterTryCreateFilmWithTooLongDescription();
		shouldReturnExceptionAfterTryCreateFilmWithoutReleaseDate();
		shouldReturnExceptionAfterTryCreateFilmWithTooEarlyReleaseDate();
		shouldReturnExceptionAfterTryCreateFilmWithReleaseDateAtFuture();
		shouldReturnExceptionAfterTryCreateFilmWithEmptyDuration();
		shouldReturnExceptionAfterTryCreateFilmWithNegativeDuration();
		shouldReturnExceptionAfterTryCreateFilmWithNullableDuration();
		shouldReturnExceptionAfterTryCreateFilmWithNullableFilm();
		shouldReturnExceptionAfterTryCreateFilmWithoutName();
	}

	@Test
	@DisplayName("Правильное обновление фильма.")
	void updateFilmSuccessTest() {
		shouldSaveActualFilmAfterUpdate();
		shouldSaveOldFilmFieldsAfterUpdateWithoutThisFields();
	}

	@Test
	@DisplayName("Неправильное обновление фильма.")
	void updateFilmFailureTest() {
		shouldReturnExceptionAfterTryUpdateFilmWithoutId();
		shouldReturnExceptionAfterTryUpdateFilmWithLostId();
		shouldReturnExceptionAfterTryUpdateFilmWithEmptyFilmName();
		shouldReturnExceptionAfterUpdateFilmWithTooLongDescription();
		shouldReturnExceptionAfterTryUpdateFilmWithTooEarlyReleaseDate();
		shouldReturnExceptionAfterTryUpdateFilmWithReleaseDateAtFuture();
		shouldReturnExceptionAfterTryUpdateFilmWithNegativeDuration();
		shouldReturnExceptionAfterTryUpdateFilmWithNullableDuration();
	}

	/**
	 * -----------------------------------------------------------------------------
	 */

	@SortedPosition(method = "createUserSuccessTest", position = 1)
	private void shouldSaveActualUserAfterCreate() {
		userService.create(User.builder()
				.login("User2")
				.email("user2@main.com")
				.birthday(LocalDate.of(2000, 12, 20))
				.build());
		User user = userService.findAll().stream().filter(u -> u.getId() == 2)
				.findFirst().orElse(User.builder().build());

		Assertions.assertAll(
				() -> assertEquals("User2", user.getName()),
				() -> assertEquals("User2", user.getLogin()),
				() -> assertEquals("user2@main.com", user.getEmail()),
				() -> assertEquals("2000-12-20", user.getBirthday().toString())
		);
	}

	@SortedPosition(method = "createUserSuccessTest", position = 2)
	private void shouldSaveUserNameOfNewLoginAfterCreateUserWithEmptyUserName() {
		userService.create(User.builder()
				.name("")
				.login("User3")
				.email("user3@main.com")
				.birthday(LocalDate.of(2001, 10, 7))
				.build());
		User user = userService.findAll().stream().filter(u -> u.getId() == 3)
				.findFirst().orElse(User.builder().build());

		Assertions.assertAll(
				() -> assertEquals("User3", user.getName()),
				() -> assertEquals("User3", user.getLogin()),
				() -> assertEquals("user3@main.com", user.getEmail()),
				() -> assertEquals("2001-10-07", user.getBirthday().toString())
		);
	}

	/**
	 * -----------------------------------------------------------------------------
	 */

	@SortedPosition(method = "createUserFailureTest", position = 1)
	private void shouldReturnExceptionAfterTryCreateUserWithEmptyEmail() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> userService.create(User.builder()
						.login("User4")
						.email("")
						.birthday(LocalDate.of(1995, 12, 20))
						.build())
		);
		assertEquals("Электронная почта не может быть пустой.", exception.getMessage());
	}

	@SortedPosition(method = "createUserFailureTest", position = 2)
	private void shouldReturnExceptionAfterTryCreateUserWithNotValidEmail() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> userService.create(User.builder()
						.login("User4")
						.email("user4main.com")
						.birthday(LocalDate.of(1995, 12, 20))
						.build())
		);
		assertEquals("Электронная почта должна содержать символ '@'.", exception.getMessage());
	}

	@SortedPosition(method = "createUserFailureTest", position = 3)
	private void shouldReturnExceptionAfterTryCreateUserWithDuplicatedEmail() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> userService.create(User.builder()
						.login("User4")
						.email("user1@main.com")
						.birthday(LocalDate.of(1995, 12, 20))
						.build())
		);
		assertEquals("Эта электронная почта user1@main.com уже используется.", exception.getMessage());
	}

	@SortedPosition(method = "createUserFailureTest", position = 4)
	private void shouldReturnExceptionAfterTryCreateUserWithEmptyLogin() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> userService.create(User.builder()
						.login("")
						.email("user4@main.com")
						.birthday(LocalDate.of(1995, 12, 20))
						.build())
		);
		assertEquals("Логин не может быть пустым.", exception.getMessage());
	}

	@SortedPosition(method = "createUserFailureTest", position = 5)
	private void shouldReturnExceptionAfterTryCreateUserWithNotValidLogin() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> userService.create(User.builder()
						.login("User 3")
						.email("user4@main.com")
						.birthday(LocalDate.of(1995, 12, 20))
						.build())
		);
		assertEquals("Логин не должен содержать пробелов.", exception.getMessage());
	}

	@SortedPosition(method = "createUserFailureTest", position = 6)
	private void shouldReturnExceptionAfterTryCreateUserWithBirthdayAtFuture() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> userService.create(User.builder()
						.login("User4")
						.email("user4@main.com")
						.birthday(LocalDate.of(3335, 12, 20))
						.build())
		);
		assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());
	}

	@SortedPosition(method = "createUserFailureTest", position = 7)
	private void shouldReturnExceptionAfterTryCreateUserWithoutBirthday() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> userService.create(User.builder()
						.login("User4")
						.email("user4@main.com")
						.build())
		);
		assertEquals("Дата рождения должна быть указана.", exception.getMessage());
	}


	@SortedPosition(method = "createUserFailureTest", position = 8)
	private void shouldReturnExceptionAfterTryCreateUserWithoutLogin() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> userService.create(User.builder()
						.birthday(LocalDate.of(1995, 12, 20))
						.email("user4@main.com")
						.build())
		);
		assertEquals("Логин должен быть указан.", exception.getMessage());
	}

	@SortedPosition(method = "createUserFailureTest", position = 9)
	private void shouldReturnExceptionAfterTryCreateUserWithoutEmail() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> userService.create(User.builder()
						.login("User4")
						.birthday(LocalDate.of(1995, 12, 20))
						.build())
		);
		assertEquals("Электронная почта должна быть указана.", exception.getMessage());
	}

	/**
	 * -----------------------------------------------------------------------------
	 */

	@SortedPosition(method = "updateUserSuccessTest", position = 1)
	private void shouldSaveActualUserAfterUpdate() {
		userService.update(User.builder()
				.id(1L)
				.name("Updated username")
				.email("updated_name@main.com")
				.login("UpdatedLogin")
				.birthday(LocalDate.parse("1999-12-20"))
				.build()
		);
		User user = userService.findAll().stream().findFirst().orElse(User.builder().build());

		Assertions.assertAll(
				() -> assertEquals("Updated username", user.getName()),
				() -> assertEquals("updated_name@main.com", user.getEmail()),
				() -> assertEquals("UpdatedLogin", user.getLogin()),
				() -> assertEquals("1999-12-20", user.getBirthday().toString())
		);
	}

	@SortedPosition(method = "updateUserSuccessTest", position = 2)
	private void shouldSaveUserNameOfNewLoginAfterUpdateUserWithEmptyUserName() {
		userService.update(User.builder()
				.id(1L)
				.name("")
				.login("UpdatedLogin111")
				.build()
		);
		User user = userService.findAll().stream().findFirst().orElse(User.builder().build());

		Assertions.assertAll(
				() -> assertEquals("UpdatedLogin111", user.getName()),
				() -> assertEquals("updated_name@main.com", user.getEmail()),
				() -> assertEquals("UpdatedLogin111", user.getLogin()),
				() -> assertEquals("1999-12-20", user.getBirthday().toString())
		);
	}

	@SortedPosition(method = "updateUserSuccessTest", position = 3)
	private void shouldSaveOldUserEqualsNewLoginNameAfterUpdateUserWithoutUserName() {
		userService.update(User.builder()
				.id(1L)
				.login("UpdatedLogin777")
				.build()
		);
		User user = userService.findAll().stream().findFirst().orElse(User.builder().build());

		Assertions.assertAll(
				() -> assertEquals("UpdatedLogin111", user.getName()),
				() -> assertEquals("updated_name@main.com", user.getEmail()),
				() -> assertEquals("UpdatedLogin777", user.getLogin()),
				() -> assertEquals("1999-12-20", user.getBirthday().toString())
		);
	}

	@SortedPosition(method = "updateUserSuccessTest", position = 4)
	private void shouldSaveOldUserFieldsAfterUpdateWithoutThisFields() {
		userService.update(User.builder()
				.id(1L)
				.build()
		);
		User user = userService.findAll().stream().findFirst().orElse(User.builder().build());

		Assertions.assertAll(
				() -> assertEquals("UpdatedLogin111", user.getName()),
				() -> assertEquals("updated_name@main.com", user.getEmail()),
				() -> assertEquals("UpdatedLogin777", user.getLogin()),
				() -> assertEquals("1999-12-20", user.getBirthday().toString())
		);
	}

	/**
	 * -----------------------------------------------------------------------------
	 */

	@SortedPosition(method = "updateUserFailureTest", position = 1)
	private void shouldReturnExceptionAfterTryUpdateUserWithoutId() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> userService.update(User.builder()
						.name("Frank")
						.build()
				));
		assertEquals("Id должен быть указан.", exception.getMessage());
	}

	@SortedPosition(method = "updateUserFailureTest", position = 2)
	private void shouldReturnExceptionAfterTryUpdateUserWithLostId() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> userService.update(User.builder()
						.id(3L)
						.name("Frank")
						.build()
				));
		assertEquals("Пользователь с id = 3 не найден.", exception.getMessage());
	}

	@SortedPosition(method = "updateUserFailureTest", position = 3)
	private void shouldReturnExceptionAfterTryUpdateUserWithEmptyLogin() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> userService.update(User.builder()
						.id(1L)
						.login("")
						.build()
				));
		assertEquals("Логин не может быть пустым.", exception.getMessage());
	}

	@SortedPosition(method = "updateUserFailureTest", position = 4)
	private void shouldReturnExceptionAfterTryUpdateUserWithNotValidLogin() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> userService.update(User.builder()
						.id(1L)
						.name("UpdatedName")
						.email("updated_name@main.com")
						.login("Not valid login")
						.birthday(LocalDate.parse("1999-12-20"))
						.build()
				));
		assertEquals("Логин не должен содержать пробелов.", exception.getMessage());
	}

	@SortedPosition(method = "updateUserFailureTest", position = 5)
	private void shouldReturnExceptionAfterTryUpdateUserWithEmptyEmail() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> userService.update(User.builder()
						.id(1L)
						.email("")
						.build()
				));
		assertEquals("Электронная почта не может быть пустой.", exception.getMessage());
	}

	@SortedPosition(method = "updateUserFailureTest", position = 6)
	private void shouldReturnExceptionAfterTryUpdateUserWithNotValidEmail() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> userService.update(User.builder()
						.id(1L)
						.email("n333ayj99i--kjj")
						.build()
				));
		assertEquals("Электронная почта должна содержать символ '@'.", exception.getMessage());
	}

	@SortedPosition(method = "updateUserFailureTest", position = 7)
	private void shouldReturnExceptionAfterTryUpdateUserWithDuplicatedEmail() {
		userService.create(User.builder()
				.name("Admin_id2")
				.email("admin@main.com")
				.login("userAdmin")
				.birthday(LocalDate.parse("1963-12-20"))
				.build()
		);

		DuplicatedDataException exception = assertThrows(DuplicatedDataException.class,
				() -> userService.update(User.builder()
						.id(1L)
						.email("admin@main.com")
						.build()
				));
		assertEquals("Эта электронная почта admin@main.com уже используется.", exception.getMessage());

	}

	/**
	 * -----------------------------------------------------------------------------
	 */

	@SortedPosition(method = "createFilmSuccessTest", position = 1)
	private void shouldSaveActualFilmAfterCreate() {
		filmService.create(Film.builder()
				.name("Film2")
				.description("this is description for film2")
				.releaseDate(LocalDate.of(2020, 11, 21))
				.duration(Duration.ofMinutes(35))
				.build());
		Film film = filmService.findAll().stream().filter(f -> f.getId() == 2)
				.findFirst().orElse(Film.builder().build());

		Assertions.assertAll(
				() -> assertEquals("Film2", film.getName()),
				() -> assertEquals("this is description for film2", film.getDescription()),
				() -> assertEquals("2020-11-21", film.getReleaseDate().toString()),
				() -> assertEquals(Duration.ofMinutes(35), film.getDuration())
		);
	}

	/**
	 * -----------------------------------------------------------------------------
	 */

	@SortedPosition(method = "createFilmFailureTest", position = 1)
	private void shouldReturnExceptionAfterTryCreateFilmWithEmptyName() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> filmService.create(Film.builder()
						.name("")
						.releaseDate(LocalDate.of(2010, 1, 2))
						.duration(Duration.ofMinutes(145))
						.build()
				));
		assertEquals("Название не может быть пустым.", exception.getMessage());
	}

	@SortedPosition(method = "createFilmFailureTest", position = 2)
	private void shouldReturnExceptionAfterTryCreateFilmWithTooLongDescription() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> filmService.create(Film.builder()
						.name("XXX")
						.description("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
								"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
								"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
								"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
						.releaseDate(LocalDate.of(2010, 1, 2))
						.duration(Duration.ofMinutes(423))
						.build()
				));
		assertEquals("Максимальная длина описания — 200 символов.", exception.getMessage());
	}

	@SortedPosition(method = "createFilmFailureTest", position = 3)
	private void shouldReturnExceptionAfterTryCreateFilmWithoutReleaseDate() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> filmService.create(Film.builder()
						.name("XXX")
						.build()
				));
		assertEquals("Дата релиза должна быть указана.", exception.getMessage());
	}

	@SortedPosition(method = "createFilmFailureTest", position = 4)
	private void shouldReturnExceptionAfterTryCreateFilmWithTooEarlyReleaseDate() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> filmService.create(Film.builder()
						.name("XXX")
						.releaseDate(LocalDate.of(1895, 12, 27))
						.duration(Duration.ofMinutes(25))
						.build()
				));
		assertEquals("Дата релиза должна быть не раньше 28 декабря 1895 года.", exception.getMessage());
	}

	@SortedPosition(method = "createFilmFailureTest", position = 5)
	private void shouldReturnExceptionAfterTryCreateFilmWithReleaseDateAtFuture() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> filmService.create(Film.builder()
						.name("XXX")
						.releaseDate(LocalDate.of(3895, 12, 27))
						.duration(Duration.ofMinutes(90))
						.build()
				));
		assertEquals("Дата релиза не должна быть в будущем.", exception.getMessage());
	}

	@SortedPosition(method = "createFilmFailureTest", position = 6)
	private void shouldReturnExceptionAfterTryCreateFilmWithEmptyDuration() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> filmService.create(Film.builder()
						.name("XXX")
						.releaseDate(LocalDate.of(2021, 12, 27))
						.build()
				));
		assertEquals("Продолжительность фильма должна быть указана.", exception.getMessage());
	}

	@SortedPosition(method = "createFilmFailureTest", position = 7)
	private void shouldReturnExceptionAfterTryCreateFilmWithNegativeDuration() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> filmService.create(Film.builder()
						.name("XXX")
						.releaseDate(LocalDate.of(2021, 12, 27))
						.duration(Duration.ofMinutes(-1))
						.build()
				));
		assertEquals("Продолжительность фильма должна быть положительным числом.", exception.getMessage());
	}

	@SortedPosition(method = "createFilmFailureTest", position = 8)
	private void shouldReturnExceptionAfterTryCreateFilmWithNullableDuration() {
		RuntimeException exception1 = assertThrows(RuntimeException.class,
				() -> filmService.create(Film.builder()
						.name("XXX")
						.releaseDate(LocalDate.of(2021, 12, 27))
						.duration(Duration.ofMinutes(0))
						.build()
				));
		assertEquals("Продолжительность фильма должна быть положительным числом.", exception1.getMessage());
	}

	@SortedPosition(method = "createFilmFailureTest", position = 9)
	private void shouldReturnExceptionAfterTryCreateFilmWithNullableFilm() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> filmService.update(null));
		assertEquals("Входные данные фильма не распознаны.", exception.getMessage());
	}

	@SortedPosition(method = "createFilmFailureTest", position = 10)
	private void shouldReturnExceptionAfterTryCreateFilmWithoutName() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> filmService.create(Film.builder()
						.releaseDate(LocalDate.of(2021, 12, 27))
						.duration(Duration.ofMinutes(10))
						.build()
				));
		assertEquals("Название должно быть указано.", exception.getMessage());
	}

	/**
	 * -----------------------------------------------------------------------------
	 */

	@SortedPosition(method = "updateFilmSuccessTest", position = 1)
	private void shouldSaveActualFilmAfterUpdate() {
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

	@SortedPosition(method = "updateFilmSuccessTest", position = 2)
	private void shouldSaveOldFilmFieldsAfterUpdateWithoutThisFields() {
		filmService.update(Film.builder()
				.id(1L)
				.build());
		Film film = filmService.findAll().stream().findFirst().orElse(Film.builder().build());

		Assertions.assertAll(
				() -> assertEquals("UpdatedName", film.getName()),
				() -> assertEquals("this is updated description", film.getDescription()),
				() -> assertEquals("2019-11-21", film.getReleaseDate().toString()),
				() -> assertEquals(Duration.ofMinutes(37), film.getDuration())
		);
	}

	/**
	 * -----------------------------------------------------------------------------
	 */

	@SortedPosition(method = "updateFilmFailureTest", position = 1)
	private void shouldReturnExceptionAfterTryUpdateFilmWithoutId() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> filmService.update(Film.builder()
						.name("XXX")
						.releaseDate(LocalDate.of(2021, 12, 27))
						.build()
				));
		assertEquals("Id должен быть указан.", exception.getMessage());
	}

	@SortedPosition(method = "updateFilmFailureTest", position = 2)
	private void shouldReturnExceptionAfterTryUpdateFilmWithLostId() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> filmService.update(Film.builder()
						.id(777L)
						.name("XXX")
						.releaseDate(LocalDate.of(2021, 12, 27))
						.build()
				));
		assertEquals("Фильм с id = 777 не найден.", exception.getMessage());
	}

	@SortedPosition(method = "updateFilmFailureTest", position = 3)
	private void shouldReturnExceptionAfterTryUpdateFilmWithEmptyFilmName() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> filmService.update(Film.builder()
						.id(1L)
						.name("")
						.releaseDate(LocalDate.of(2021, 12, 27))
						.build()
				));
		assertEquals("Название не может быть пустым.", exception.getMessage());
	}

	@SortedPosition(method = "updateFilmFailureTest", position = 4)
	private void shouldReturnExceptionAfterUpdateFilmWithTooLongDescription() {
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

	@SortedPosition(method = "updateFilmFailureTest", position = 5)
	private void shouldReturnExceptionAfterTryUpdateFilmWithTooEarlyReleaseDate() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> filmService.update(Film.builder()
						.id(1L)
						.releaseDate(LocalDate.of(1895, 1, 27))
						.build()
				));
		assertEquals("Дата релиза должна быть не раньше 28 декабря 1895 года.", exception.getMessage());
	}

	@SortedPosition(method = "updateFilmFailureTest", position = 6)
	private void shouldReturnExceptionAfterTryUpdateFilmWithReleaseDateAtFuture() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> filmService.update(Film.builder()
						.id(1L)
						.releaseDate(LocalDate.of(3895, 1, 27))
						.build()
				));
		assertEquals("Дата релиза не должна быть в будущем.", exception.getMessage());
	}

	@SortedPosition(method = "updateFilmFailureTest", position = 7)
	private void shouldReturnExceptionAfterTryUpdateFilmWithNegativeDuration() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> filmService.update(Film.builder()
						.id(1L)
						.duration(Duration.ofMinutes(-100))
						.build()
				));
		assertEquals("Продолжительность фильма должна быть положительным числом.", exception.getMessage());
	}

	@SortedPosition(method = "updateFilmFailureTest", position = 8)
	private void shouldReturnExceptionAfterTryUpdateFilmWithNullableDuration() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> filmService.update(Film.builder()
						.id(1L)
						.duration(Duration.ofMinutes(0))
						.build()
				));
		assertEquals("Продолжительность фильма должна быть положительным числом.", exception.getMessage());
	}

	/**
	 * -----------------------------------------------------------------------------
	 */

	@SortedPosition(method = "getUserSuccessTest", position = 1)
	private void shouldReturnActualUserAfterGet() {
		User user = userService.get(1L);

		Assertions.assertAll(
				() -> assertEquals("User1", user.getName()),
				() -> assertEquals("User1", user.getLogin()),
				() -> assertEquals("user1@main.com", user.getEmail()),
				() -> assertEquals("2003-10-02", user.getBirthday().toString())
		);
	}
}
