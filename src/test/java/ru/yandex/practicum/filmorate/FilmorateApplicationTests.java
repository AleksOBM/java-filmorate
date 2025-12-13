package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.annotation.SortedPosition;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {

    private UserController userController;
    private FilmController filmController;

    @BeforeEach
    void beforeEach() {
        userController = new UserController();
        filmController = new FilmController();

        userController.create(User.builder()
                .login("User1")
                .email("user1@main.com")
                .birthday(LocalDate.of(2003, 10, 2))
                .build());

        filmController.create(Film.builder()
                .name("Film1")
                .description("this is description for film1")
                .releaseDate(LocalDate.of(2018, 1, 11))
                .duration(135)
                .build());
    }

    @Test
    void createUserSuccessTest() {
        shouldReturnActualUserAfterCreate();
        shouldReturnUserNameOfNewLoginAfterCreateUserWithEmptyUserName();
    }

    @Test
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
    void updateUserSuccessTest() {
        shouldReturnActualUserAfterUpdate();
        shouldReturnUserNameOfNewLoginAfterUpdateUserWithEmptyUserName();
        shouldReturnOldUserEqualsNewLoginNameAfterUpdateUserWithoutUserName();
        shouldReturnOldUserFieldsAfterUpdateWithoutThisFields();
    }

    @Test
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
    void createFilmSuccessTest() {
        shouldReturnActualFilmAfterCreate();
    }

    @Test
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
    void updateFilmSuccessTest() {
        shouldReturnActualFilmAfterUpdate();
        shouldReturnOldFilmFieldsAfterUpdateWithoutThisFields();
    }

    @Test
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
    private void shouldReturnActualUserAfterCreate() {
        userController.create(User.builder()
                .login("User2")
                .email("user2@main.com")
                .birthday(LocalDate.of(2000, 12, 20))
                .build());
        User user = userController.findAll().stream().filter(u -> u.getId() == 2)
                .findFirst().orElse(User.builder().build());
        assertEquals("User2", user.getName());
        assertEquals("User2", user.getLogin());
        assertEquals("user2@main.com", user.getEmail());
        assertEquals("2000-12-20", user.getBirthday().toString());
    }


    @SortedPosition(method = "createUserSuccessTest", position = 2)
    private void shouldReturnUserNameOfNewLoginAfterCreateUserWithEmptyUserName() {
        userController.create(User.builder()
                .name("")
                .login("User3")
                .email("user3@main.com")
                .birthday(LocalDate.of(2001, 10, 7))
                .build());
        User user = userController.findAll().stream().filter(u -> u.getId() == 3)
                .findFirst().orElse(User.builder().build());
        assertEquals("User3", user.getName());
        assertEquals("User3", user.getLogin());
        assertEquals("user3@main.com", user.getEmail());
        assertEquals("2001-10-07", user.getBirthday().toString());
    }

    /**
     * -----------------------------------------------------------------------------
     */

    @SortedPosition(method = "createUserFailureTest", position = 1)
    private void shouldReturnExceptionAfterTryCreateUserWithEmptyEmail() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userController.create(User.builder()
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
                () -> userController.create(User.builder()
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
                () -> userController.create(User.builder()
                        .login("User4")
                        .email("user1@main.com")
                        .birthday(LocalDate.of(1995, 12, 20))
                        .build())
        );
        assertEquals("Эта электронная почта уже используется.", exception.getMessage());
    }

    @SortedPosition(method = "createUserFailureTest", position = 4)
    private void shouldReturnExceptionAfterTryCreateUserWithEmptyLogin() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userController.create(User.builder()
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
                () -> userController.create(User.builder()
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
                () -> userController.create(User.builder()
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
                () -> userController.create(User.builder()
                        .login("User4")
                        .email("user4@main.com")
                        .build())
        );
        assertEquals("Дата рождения должна быть указана.", exception.getMessage());
    }


    @SortedPosition(method = "createUserFailureTest", position = 8)
    private void shouldReturnExceptionAfterTryCreateUserWithoutLogin() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userController.create(User.builder()
                        .birthday(LocalDate.of(1995, 12, 20))
                        .email("user4@main.com")
                        .build())
        );
        assertEquals("Логин должен быть указан.", exception.getMessage());
    }

    @SortedPosition(method = "createUserFailureTest", position = 9)
    private void shouldReturnExceptionAfterTryCreateUserWithoutEmail() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userController.create(User.builder()
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
    private void shouldReturnActualUserAfterUpdate() {
        userController.update(User.builder()
                .id(1)
                .name("Updated username")
                .email("updated_name@main.com")
                .login("UpdatedLogin")
                .birthday(LocalDate.parse("1999-12-20"))
                .build()
        );
        User user = userController.findAll().stream().findFirst().orElse(User.builder().build());
        assertEquals("Updated username", user.getName());
        assertEquals("updated_name@main.com", user.getEmail());
        assertEquals("UpdatedLogin", user.getLogin());
        assertEquals("1999-12-20", user.getBirthday().toString());
    }

    @SortedPosition(method = "updateUserSuccessTest", position = 2)
    private void shouldReturnUserNameOfNewLoginAfterUpdateUserWithEmptyUserName() {
        userController.update(User.builder()
                .id(1)
                .name("")
                .login("UpdatedLogin111")
                .build()
        );
        User user = userController.findAll().stream().findFirst().orElse(User.builder().build());
        assertEquals("UpdatedLogin111", user.getName());
        assertEquals("updated_name@main.com", user.getEmail());
        assertEquals("UpdatedLogin111", user.getLogin());
        assertEquals("1999-12-20", user.getBirthday().toString());
    }

    @SortedPosition(method = "updateUserSuccessTest", position = 3)
    private void shouldReturnOldUserEqualsNewLoginNameAfterUpdateUserWithoutUserName() {
        userController.update(User.builder()
                .id(1)
                .login("UpdatedLogin777")
                .build()
        );
        User user = userController.findAll().stream().findFirst().orElse(User.builder().build());
        assertEquals("UpdatedLogin111", user.getName());
        assertEquals("updated_name@main.com", user.getEmail());
        assertEquals("UpdatedLogin777", user.getLogin());
        assertEquals("1999-12-20", user.getBirthday().toString());
    }

    @SortedPosition(method = "updateUserSuccessTest", position = 4)
    private void shouldReturnOldUserFieldsAfterUpdateWithoutThisFields() {
        userController.update(User.builder()
                .id(1)
                .build()
        );
        User user = userController.findAll().stream().findFirst().orElse(User.builder().build());
        assertEquals("UpdatedLogin111", user.getName());
        assertEquals("updated_name@main.com", user.getEmail());
        assertEquals("UpdatedLogin777", user.getLogin());
        assertEquals("1999-12-20", user.getBirthday().toString());
    }

    /**
     * -----------------------------------------------------------------------------
     */

    @SortedPosition(method = "updateUserFailureTest", position = 1)
    private void shouldReturnExceptionAfterTryUpdateUserWithoutId() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userController.update(User.builder()
                        .name("Frank")
                        .build()
                ));
        assertEquals("Id должен быть указан.", exception.getMessage());
    }

    @SortedPosition(method = "updateUserFailureTest", position = 2)
    private void shouldReturnExceptionAfterTryUpdateUserWithLostId() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userController.update(User.builder()
                        .id(3)
                        .name("Frank")
                        .build()
                ));
        assertEquals("Пользователь с id = 3 не найден.", exception.getMessage());
    }

    @SortedPosition(method = "updateUserFailureTest", position = 3)
    private void shouldReturnExceptionAfterTryUpdateUserWithEmptyLogin() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userController.update(User.builder()
                        .id(1)
                        .login("")
                        .build()
                ));
        assertEquals("Логин не может быть пустым.", exception.getMessage());
    }

    @SortedPosition(method = "updateUserFailureTest", position = 4)
    private void shouldReturnExceptionAfterTryUpdateUserWithNotValidLogin() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userController.update(User.builder()
                        .id(1)
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
                () -> userController.update(User.builder()
                        .id(1)
                        .email("")
                        .build()
                ));
        assertEquals("Электронная почта не может быть пустой.", exception.getMessage());
    }

    @SortedPosition(method = "updateUserFailureTest", position = 6)
    private void shouldReturnExceptionAfterTryUpdateUserWithNotValidEmail() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userController.update(User.builder()
                        .id(1)
                        .email("n333ayj99i--kjj")
                        .build()
                ));
        assertEquals("Электронная почта должна содержать символ '@'.", exception.getMessage());
    }

    @SortedPosition(method = "updateUserFailureTest", position = 7)
    private void shouldReturnExceptionAfterTryUpdateUserWithDuplicatedEmail() {
        userController.create(User.builder()
                .name("Admin_id2")
                .email("admin@main.com")
                .login("userAdmin")
                .birthday(LocalDate.parse("1963-12-20"))
                .build()
        );

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userController.update(User.builder()
                        .id(1)
                        .email("admin@main.com")
                        .build()
                ));
        assertEquals("Эта электронная почта уже используется.", exception.getMessage());
    }

    /**
     * -----------------------------------------------------------------------------
     */

    @SortedPosition(method = "createFilmSuccessTest", position = 1)
    private void shouldReturnActualFilmAfterCreate() {
        filmController.create(Film.builder()
                .name("Film2")
                .description("this is description for film2")
                .releaseDate(LocalDate.of(2020, 11, 21))
                .duration(35)
                .build());
        Film film = filmController.findAll().stream().filter(f -> f.getId() == 2)
                .findFirst().orElse(Film.builder().build());
        assertEquals("Film2", film.getName());
        assertEquals("this is description for film2", film.getDescription());
        assertEquals("2020-11-21", film.getReleaseDate().toString());
        assertEquals(35, film.getDuration());
    }

    /**
     * -----------------------------------------------------------------------------
     */

    @SortedPosition(method = "createFilmFailureTest", position = 1)
    private void shouldReturnExceptionAfterTryCreateFilmWithEmptyName() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> filmController.create(Film.builder()
                        .name("")
                        .releaseDate(LocalDate.of(2010, 1, 2))
                        .duration(145)
                        .build()
                ));
        assertEquals("Название не может быть пустым.", exception.getMessage());
    }

    @SortedPosition(method = "createFilmFailureTest", position = 2)
    private void shouldReturnExceptionAfterTryCreateFilmWithTooLongDescription() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> filmController.create(Film.builder()
                        .name("XXX")
                        .description("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
                                "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
                                "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
                                "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
                        .releaseDate(LocalDate.of(2010, 1, 2))
                        .duration(423)
                        .build()
                ));
        assertEquals("Максимальная длина описания — 200 символов.", exception.getMessage());
    }

    @SortedPosition(method = "createFilmFailureTest", position = 3)
    private void shouldReturnExceptionAfterTryCreateFilmWithoutReleaseDate() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> filmController.create(Film.builder()
                        .name("XXX")
                        .build()
                ));
        assertEquals("Дата релиза должна быть указана.", exception.getMessage());
    }

    @SortedPosition(method = "createFilmFailureTest", position = 4)
    private void shouldReturnExceptionAfterTryCreateFilmWithTooEarlyReleaseDate() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> filmController.create(Film.builder()
                        .name("XXX")
                        .releaseDate(LocalDate.of(1895, 12, 27))
                        .duration(25)
                        .build()
                ));
        assertEquals("Дата релиза должна быть не раньше 28 декабря 1895 года.", exception.getMessage());
    }

    @SortedPosition(method = "createFilmFailureTest", position = 5)
    private void shouldReturnExceptionAfterTryCreateFilmWithReleaseDateAtFuture() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> filmController.create(Film.builder()
                        .name("XXX")
                        .releaseDate(LocalDate.of(3895, 12, 27))
                        .duration(90)
                        .build()
                ));
        assertEquals("Дата релиза не должна быть в будущем.", exception.getMessage());
    }

    @SortedPosition(method = "createFilmFailureTest", position = 6)
    private void shouldReturnExceptionAfterTryCreateFilmWithEmptyDuration() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> filmController.create(Film.builder()
                        .name("XXX")
                        .releaseDate(LocalDate.of(2021, 12, 27))
                        .build()
                ));
        assertEquals("Продолжительность фильма должна быть указана.", exception.getMessage());
    }

    @SortedPosition(method = "createFilmFailureTest", position = 7)
    private void shouldReturnExceptionAfterTryCreateFilmWithNegativeDuration() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> filmController.create(Film.builder()
                        .name("XXX")
                        .releaseDate(LocalDate.of(2021, 12, 27))
                        .duration(-1)
                        .build()
                ));
        assertEquals("Продолжительность фильма должна быть положительным числом.", exception.getMessage());
    }

    @SortedPosition(method = "createFilmFailureTest", position = 8)
    private void shouldReturnExceptionAfterTryCreateFilmWithNullableDuration() {
        RuntimeException exception1 = assertThrows(RuntimeException.class,
                () -> filmController.create(Film.builder()
                        .name("XXX")
                        .releaseDate(LocalDate.of(2021, 12, 27))
                        .duration(0)
                        .build()
                ));
        assertEquals("Продолжительность фильма должна быть положительным числом.", exception1.getMessage());
    }

    @SortedPosition(method = "createFilmFailureTest", position = 9)
    private void shouldReturnExceptionAfterTryCreateFilmWithNullableFilm() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> filmController.update(null));
        assertEquals("Входные данные фильма не распознаны.", exception.getMessage());
    }

    @SortedPosition(method = "createFilmFailureTest", position = 10)
    private void shouldReturnExceptionAfterTryCreateFilmWithoutName() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> filmController.create(Film.builder()
                        .releaseDate(LocalDate.of(2021, 12, 27))
                        .duration(10)
                        .build()
                ));
        assertEquals("Название должно быть указано.", exception.getMessage());
    }

    /**
     * -----------------------------------------------------------------------------
     */

    @SortedPosition(method = "updateFilmSuccessTest", position = 1)
    private void shouldReturnActualFilmAfterUpdate() {
        filmController.update(Film.builder()
                .id(1)
                .name("UpdatedName")
                .description("this is updated description")
                .releaseDate(LocalDate.of(2019, 11, 21))
                .duration(37)
                .build());
        Film film = filmController.findAll().stream().findFirst().orElse(Film.builder().build());
        assertEquals("UpdatedName", film.getName());
        assertEquals("this is updated description", film.getDescription());
        assertEquals("2019-11-21", film.getReleaseDate().toString());
        assertEquals(37, film.getDuration());
    }

    @SortedPosition(method = "updateFilmSuccessTest", position = 2)
    private void shouldReturnOldFilmFieldsAfterUpdateWithoutThisFields() {
        filmController.update(Film.builder()
                .id(1)
                .build());
        Film film = filmController.findAll().stream().findFirst().orElse(Film.builder().build());
        assertEquals("UpdatedName", film.getName());
        assertEquals("this is updated description", film.getDescription());
        assertEquals("2019-11-21", film.getReleaseDate().toString());
        assertEquals(37, film.getDuration());
    }

    /**
     * -----------------------------------------------------------------------------
     */

    @SortedPosition(method = "updateFilmFailureTest", position = 1)
    private void shouldReturnExceptionAfterTryUpdateFilmWithoutId() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> filmController.update(Film.builder()
                        .name("XXX")
                        .releaseDate(LocalDate.of(2021, 12, 27))
                        .build()
                ));
        assertEquals("Id должен быть указан.", exception.getMessage());
    }

    @SortedPosition(method = "updateFilmFailureTest", position = 2)
    private void shouldReturnExceptionAfterTryUpdateFilmWithLostId() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> filmController.update(Film.builder()
                        .id(777)
                        .name("XXX")
                        .releaseDate(LocalDate.of(2021, 12, 27))
                        .build()
                ));
        assertEquals("Пользователь с id = 777 не найден.", exception.getMessage());
    }

    @SortedPosition(method = "updateFilmFailureTest", position = 3)
    private void shouldReturnExceptionAfterTryUpdateFilmWithEmptyFilmName() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> filmController.update(Film.builder()
                        .id(1)
                        .name("")
                        .releaseDate(LocalDate.of(2021, 12, 27))
                        .build()
                ));
        assertEquals("Название не может быть пустым.", exception.getMessage());
    }

    @SortedPosition(method = "updateFilmFailureTest", position = 4)
    private void shouldReturnExceptionAfterUpdateFilmWithTooLongDescription() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> filmController.update(Film.builder()
                        .id(1)
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
                () -> filmController.update(Film.builder()
                        .id(1)
                        .releaseDate(LocalDate.of(1895, 1, 27))
                        .build()
                ));
        assertEquals("Дата релиза должна быть не раньше 28 декабря 1895 года.", exception.getMessage());
    }

    @SortedPosition(method = "updateFilmFailureTest", position = 6)
    private void shouldReturnExceptionAfterTryUpdateFilmWithReleaseDateAtFuture() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> filmController.update(Film.builder()
                        .id(1)
                        .releaseDate(LocalDate.of(3895, 1, 27))
                        .build()
                ));
        assertEquals("Дата релиза не должна быть в будущем.", exception.getMessage());
    }

    @SortedPosition(method = "updateFilmFailureTest", position = 7)
    private void shouldReturnExceptionAfterTryUpdateFilmWithNegativeDuration() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> filmController.update(Film.builder()
                        .id(1)
                        .duration(-100)
                        .build()
                ));
        assertEquals("Продолжительность фильма должна быть положительным числом.", exception.getMessage());
    }

    @SortedPosition(method = "updateFilmFailureTest", position = 8)
    private void shouldReturnExceptionAfterTryUpdateFilmWithNullableDuration() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> filmController.update(Film.builder()
                        .id(1)
                        .duration(0)
                        .build()
                ));
        assertEquals("Продолжительность фильма должна быть положительным числом.", exception.getMessage());
    }
}
