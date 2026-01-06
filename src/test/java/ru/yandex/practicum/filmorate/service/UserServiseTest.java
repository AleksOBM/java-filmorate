package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.servise.FriendsAction;
import ru.yandex.practicum.filmorate.servise.IdentifyService;
import ru.yandex.practicum.filmorate.servise.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiseTest {

	private UserService userService;

	@BeforeEach
	void beforeEach() {
		UserStorage userStorage = new InMemoryUserStorage(new HashMap<>(), new IdentifyService());
		userService = new UserService(userStorage);

		userService.create(User.builder()
				.login("User1")
				.email("user1@main.com")
				.birthday(LocalDate.of(2003, 10, 2))
				.build());
	}

	@Nested
	@DisplayName("Добавление пользователя.")
	class Create {

		@Test
		void saveUserNameOfNewLoginWhenEmptyUserName() {
			userService.create(User.builder()
					.name("")
					.login("User3")
					.email("user3@main.com")
					.birthday(LocalDate.of(2001, 10, 7))
					.build());
			User user = userService.findAll().stream().filter(u -> u.getId() == 2)
					.findFirst().orElse(User.builder().build());

			Assertions.assertAll(
					() -> assertEquals("User3", user.getName()),
					() -> assertEquals("User3", user.getLogin()),
					() -> assertEquals("user3@main.com", user.getEmail()),
					() -> assertEquals("2001-10-07", user.getBirthday().toString())
			);
		}

		@Test
		void throwsWhenUserWithoutLogin() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> userService.create(User.builder()
							.birthday(LocalDate.of(1995, 12, 20))
							.email("user4@main.com")
							.build())
			);
			assertEquals("Логин должен быть указан.", exception.getMessage());
		}

		@Test
		void throwsWhenUserWithoutEmail() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> userService.create(User.builder()
							.login("User4")
							.birthday(LocalDate.of(1995, 12, 20))
							.build())
			);
			assertEquals("Электронная почта должна быть указана.", exception.getMessage());
		}

		@Test
		void throwsWhenDuplicatedEmail() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> userService.create(User.builder()
							.login("User4")
							.email("user1@main.com")
							.birthday(LocalDate.of(1995, 12, 20))
							.build())
			);
			assertEquals("Эта электронная почта user1@main.com уже используется.", exception.getMessage());
		}

		@Test
		void throwsWhenUserWithoutBirthday() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> userService.create(User.builder()
							.login("User4")
							.email("user4@main.com")
							.build())
			);
			assertEquals("Дата рождения должна быть указана.", exception.getMessage());
		}

		void throwsWhenUserWithEmptyEmail() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> userService.create(User.builder()
							.login("User4")
							.email("")
							.birthday(LocalDate.of(1995, 12, 20))
							.build())
			);
			assertEquals("Электронная почта не может быть пустой.", exception.getMessage());
		}

		void throwsWhenUserWithNotValidEmail() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> userService.create(User.builder()
							.login("User4")
							.email("user4main.com")
							.birthday(LocalDate.of(1995, 12, 20))
							.build())
			);
			assertEquals("Электронная почта должна содержать символ '@'.", exception.getMessage());
		}

		void throwsWhenUserWithEmptyLogin() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> userService.create(User.builder()
							.login("")
							.email("user4@main.com")
							.birthday(LocalDate.of(1995, 12, 20))
							.build())
			);
			assertEquals("Логин не может быть пустым.", exception.getMessage());
		}

		void throwsWhenUserWithNotValidLogin() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> userService.create(User.builder()
							.login("User 3")
							.email("user4@main.com")
							.birthday(LocalDate.of(1995, 12, 20))
							.build())
			);
			assertEquals("Логин не должен содержать пробелов.", exception.getMessage());
		}

		void throwsWhenUserWithBirthdayAtFuture() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> userService.create(User.builder()
							.login("User4")
							.email("user4@main.com")
							.birthday(LocalDate.of(3335, 12, 20))
							.build())
			);
			assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());
		}
	}

	@Nested
	@DisplayName("Обновление пользователя.")
	class Update {

		@Test
		void saveActualUser() {
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

		@Test
		void saveUserNameOfNewLoginWhenUserWithEmptyUserName() {
			userService.update(User.builder()
					.id(1L)
					.name("")
					.login("UpdatedLogin111")
					.build()
			);
			User user = userService.findAll().stream().findFirst().orElse(User.builder().build());

			Assertions.assertAll(
					() -> assertEquals("UpdatedLogin111", user.getName()),
					() -> assertEquals("user1@main.com", user.getEmail()),
					() -> assertEquals("UpdatedLogin111", user.getLogin()),
					() -> assertEquals("2003-10-02", user.getBirthday().toString())
			);
		}

		@Test
		void saveOldUserEqualsNewLoginNameWhenUserWithoutUserName() {
			userService.update(User.builder()
					.id(1L)
					.login("UpdatedLogin777")
					.build()
			);
			User user = userService.findAll().stream().findFirst().orElse(User.builder().build());

			Assertions.assertAll(
					() -> assertEquals("User1", user.getName()),
					() -> assertEquals("user1@main.com", user.getEmail()),
					() -> assertEquals("UpdatedLogin777", user.getLogin()),
					() -> assertEquals("2003-10-02", user.getBirthday().toString())
			);
		}

		@Test
		void saveOldUserFieldsWhenWithoutThisFields() {
			userService.update(User.builder()
					.id(1L)
					.build()
			);
			User user = userService.findAll().stream().findFirst().orElse(User.builder().build());

			Assertions.assertAll(
					() -> assertEquals("User1", user.getName()),
					() -> assertEquals("user1@main.com", user.getEmail()),
					() -> assertEquals("User1", user.getLogin()),
					() -> assertEquals("2003-10-02", user.getBirthday().toString())
			);
		}

		@Test
		void throwsWhenUserWithoutId() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> userService.update(User.builder()
							.name("Frank")
							.build()
					));
			assertEquals("Id должен быть указан.", exception.getMessage());
		}

		@Test
		void throwsWhenUserWithLostId() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> userService.update(User.builder()
							.id(3L)
							.name("Frank")
							.build()
					));
			assertEquals("Пользователь с id = 3 не найден.", exception.getMessage());
		}

		@Test
		void throwsWhenUserWithDuplicatedEmail() {
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

		void throwsWhenUserWithEmptyLogin() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> userService.update(User.builder()
							.id(1L)
							.login("")
							.build()
					));
			assertEquals("Логин не может быть пустым.", exception.getMessage());
		}

		void throwsWhenUserWithNotValidLogin() {
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

		void throwsWhenUserWithEmptyEmail() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> userService.update(User.builder()
							.id(1L)
							.email("")
							.build()
					));
			assertEquals("Электронная почта не может быть пустой.", exception.getMessage());
		}

		void throwsWhenUserWithNotValidEmail() {
			RuntimeException exception = assertThrows(RuntimeException.class,
					() -> userService.update(User.builder()
							.id(1L)
							.email("n333ayj99i--kjj")
							.build()
					));
			assertEquals("Электронная почта должна содержать символ '@'.", exception.getMessage());
		}
	}

	@Nested
	@DisplayName("Получение пользователя.")
	class Get {
		@Test
		void returnsActualUserWhenGetAll() {
			userService.create(User.builder()
					.login("User2")
					.email("user2@main.com")
					.birthday(LocalDate.of(2000, 12, 20))
					.build());

			Collection<User> users = userService.findAll();

			assertEquals(2, users.size());

			User user = users.stream().filter(u -> u.getId() == 1)
					.findFirst().orElse(User.builder().build());

			Assertions.assertAll(
					() -> assertEquals("User1", user.getName()),
					() -> assertEquals("User1", user.getLogin()),
					() -> assertEquals("user1@main.com", user.getEmail()),
					() -> assertEquals("2003-10-02", user.getBirthday().toString())
			);
		}

		@Test
		void returnsActualUserWhenGet() {
			User user = userService.get(1L);

			Assertions.assertAll(
					() -> assertEquals("User1", user.getName()),
					() -> assertEquals("User1", user.getLogin()),
					() -> assertEquals("user1@main.com", user.getEmail()),
					() -> assertEquals("2003-10-02", user.getBirthday().toString())
			);
		}
	}

	@Nested
	class Friends {

		@Test
		void requestFriends() {
			userService.create(User.builder()
					.login("User2")
					.email("user2@mail.ru")
					.birthday(LocalDate.parse("2020-10-20"))
					.build()
			);

			userService.changeFriends(FriendsAction.REQUEST, 1L, 2L);

			User user1 = userService.get(1L);
			User user2 = userService.get(2L);

			assertTrue(user1.getFriendsIds().isEmpty());
			assertTrue(user1.getRejectedRequests().isEmpty());
			assertTrue(user1.getIncomingFriendRequests().isEmpty());
			assertTrue(user1.getOutgoingFriendRequests().contains(2L));

			assertTrue(user2.getFriendsIds().isEmpty());
			assertTrue(user2.getRejectedRequests().isEmpty());
			assertTrue(user2.getIncomingFriendRequests().contains(1L));
			assertTrue(user2.getOutgoingFriendRequests().isEmpty());

		}

		@Test
		void requestBadFriends() {
			userService.create(User.builder()
					.login("User2")
					.email("user2@mail.ru")
					.birthday(LocalDate.parse("2020-10-20"))
					.build()
			);

			NotFoundException exception1 = assertThrows(NotFoundException.class,
					() -> userService.changeFriends(FriendsAction.REQUEST, 3L, 2L));

			assertEquals("Первый пользователь с id=3 не найден.",
					exception1.getMessage()
			);

			NotFoundException exception2 = assertThrows(NotFoundException.class,
					() -> userService.changeFriends(FriendsAction.REQUEST, 1L, 3L));

			assertEquals("Второй пользователь с id=3 не найден.",
					exception2.getMessage()
			);

			ParameterNotValidException exception3 = assertThrows(ParameterNotValidException.class,
					() -> userService.changeFriends(FriendsAction.REQUEST, 1L, 1L));

			assertEquals("У пользователей должны быть разные id.",
					exception3.getMessage()
			);

			User user1 = userService.get(1L);
			User user2 = userService.get(2L);

			assertTrue(user1.getFriendsIds().isEmpty());
			assertTrue(user1.getRejectedRequests().isEmpty());
			assertTrue(user1.getIncomingFriendRequests().isEmpty());
			assertTrue(user1.getOutgoingFriendRequests().isEmpty());

			assertTrue(user2.getFriendsIds().isEmpty());
			assertTrue(user2.getRejectedRequests().isEmpty());
			assertTrue(user2.getIncomingFriendRequests().isEmpty());
			assertTrue(user2.getOutgoingFriendRequests().isEmpty());

		}

		@Test
		void approveFriends() {
			userService.create(User.builder()
					.login("User2")
					.email("user2@mail.ru")
					.birthday(LocalDate.parse("2020-10-20"))
					.build()
			);

			userService.changeFriends(FriendsAction.REQUEST, 1L, 2L);
			userService.changeFriends(FriendsAction.APPROVE, 2L, 1L);

			User user1 = userService.get(1L);
			User user2 = userService.get(2L);

			assertTrue(user1.getFriendsIds().contains(2L));
			assertTrue(user1.getRejectedRequests().isEmpty());
			assertTrue(user1.getIncomingFriendRequests().isEmpty());
			assertTrue(user1.getOutgoingFriendRequests().isEmpty());

			assertTrue(user2.getFriendsIds().contains(1L));
			assertTrue(user2.getRejectedRequests().isEmpty());
			assertTrue(user2.getIncomingFriendRequests().isEmpty());
			assertTrue(user2.getOutgoingFriendRequests().isEmpty());

		}

		@Test
		void rejectFriends() {
			userService.create(User.builder()
					.login("User2")
					.email("user2@mail.ru")
					.birthday(LocalDate.parse("2020-10-20"))
					.build()
			);

			userService.changeFriends(FriendsAction.REQUEST, 1L, 2L);
			userService.changeFriends(FriendsAction.REJECT, 2L, 1L);

			User user1 = userService.get(1L);
			User user2 = userService.get(2L);

			assertTrue(user1.getFriendsIds().isEmpty());
			assertTrue(user1.getRejectedRequests().isEmpty());
			assertTrue(user1.getIncomingFriendRequests().isEmpty());
			assertTrue(user1.getOutgoingFriendRequests().isEmpty());

			assertTrue(user2.getFriendsIds().isEmpty());
			assertTrue(user2.getRejectedRequests().contains(1L));
			assertTrue(user2.getIncomingFriendRequests().isEmpty());
			assertTrue(user2.getOutgoingFriendRequests().isEmpty());

		}

		@Test
		void requestAlredyrejectedFriends() {
			userService.create(User.builder()
					.login("User2")
					.email("user2@mail.ru")
					.birthday(LocalDate.parse("2020-10-20"))
					.build()
			);

			userService.changeFriends(FriendsAction.REQUEST, 1L, 2L);
			userService.changeFriends(FriendsAction.REJECT, 2L, 1L);

			NotAllowedException exception = assertThrows(NotAllowedException.class,
					() -> userService.changeFriends(FriendsAction.REQUEST, 1L, 2L));

			assertEquals("Пользователь с id=2 уже отклонил запрос пользователя с id=1 ранее.",
					exception.getMessage()
			);


			User user1 = userService.get(1L);
			User user2 = userService.get(2L);

			assertTrue(user1.getFriendsIds().isEmpty());
			assertTrue(user1.getRejectedRequests().isEmpty());
			assertTrue(user1.getIncomingFriendRequests().isEmpty());
			assertTrue(user1.getOutgoingFriendRequests().isEmpty());

			assertTrue(user2.getFriendsIds().isEmpty());
			assertTrue(user2.getRejectedRequests().contains(1L));
			assertTrue(user2.getIncomingFriendRequests().isEmpty());
			assertTrue(user2.getOutgoingFriendRequests().isEmpty());

		}

		@Test
		void removeFriends() {
			userService.create(User.builder()
					.login("User2")
					.email("user2@mail.ru")
					.birthday(LocalDate.parse("2020-10-20"))
					.build()
			);

			userService.changeFriends(FriendsAction.REQUEST, 1L, 2L);
			userService.changeFriends(FriendsAction.APPROVE, 2L, 1L);
			userService.changeFriends(FriendsAction.REMOVE, 2L, 1L);

			User user1 = userService.get(1L);
			User user2 = userService.get(2L);

			assertTrue(user1.getFriendsIds().isEmpty());
			assertTrue(user1.getRejectedRequests().isEmpty());
			assertTrue(user1.getIncomingFriendRequests().isEmpty());
			assertTrue(user1.getOutgoingFriendRequests().isEmpty());

			assertTrue(user2.getFriendsIds().isEmpty());
			assertTrue(user2.getRejectedRequests().isEmpty());
			assertTrue(user2.getIncomingFriendRequests().isEmpty());
			assertTrue(user2.getOutgoingFriendRequests().isEmpty());

		}

		@Test
		void requestFriendsWithAlredyFrends() {
			userService.create(User.builder()
					.login("User2")
					.email("user2@mail.ru")
					.birthday(LocalDate.parse("2020-10-20"))
					.build()
			);

			userService.changeFriends(FriendsAction.REQUEST, 1L, 2L);
			userService.changeFriends(FriendsAction.APPROVE, 2L, 1L);

			AlredyAcceptedException exception = assertThrows(AlredyAcceptedException.class,
					() -> userService.changeFriends(FriendsAction.REQUEST, 2L, 1L));

			assertEquals("Пользователь с id=1 уже в друзьях у пользователя с id=2.",
					exception.getMessage()
			);

			User user1 = userService.get(1L);
			User user2 = userService.get(2L);

			assertTrue(user1.getFriendsIds().contains(2L));
			assertTrue(user1.getRejectedRequests().isEmpty());
			assertTrue(user1.getIncomingFriendRequests().isEmpty());
			assertTrue(user1.getOutgoingFriendRequests().isEmpty());

			assertTrue(user2.getFriendsIds().contains(1L));
			assertTrue(user2.getRejectedRequests().isEmpty());
			assertTrue(user2.getIncomingFriendRequests().isEmpty());
			assertTrue(user2.getOutgoingFriendRequests().isEmpty());

		}

		@Test
		void doubleRequestFriends() {
			userService.create(User.builder()
					.login("User2")
					.email("user2@mail.ru")
					.birthday(LocalDate.parse("2020-10-20"))
					.build()
			);

			userService.changeFriends(FriendsAction.REQUEST, 1L, 2L);

			AlredyAcceptedException exception = assertThrows(AlredyAcceptedException.class,
					() -> userService.changeFriends(FriendsAction.REQUEST, 1L, 2L));

			assertEquals("Пользователь с id=1 уже в отправлял запрос пользователю с id=2.",
					exception.getMessage()
			);

			User user1 = userService.get(1L);
			User user2 = userService.get(2L);

			assertTrue(user1.getFriendsIds().isEmpty());
			assertTrue(user1.getRejectedRequests().isEmpty());
			assertTrue(user1.getIncomingFriendRequests().isEmpty());
			assertTrue(user1.getOutgoingFriendRequests().contains(2L));

			assertTrue(user2.getFriendsIds().isEmpty());
			assertTrue(user2.getRejectedRequests().isEmpty());
			assertTrue(user2.getIncomingFriendRequests().contains(1L));
			assertTrue(user2.getOutgoingFriendRequests().isEmpty());

		}

		@Test
		void requestingMatches() {
			userService.create(User.builder()
					.login("User2")
					.email("user2@mail.ru")
					.birthday(LocalDate.parse("2020-10-20"))
					.build()
			);

			userService.changeFriends(FriendsAction.REQUEST, 1L, 2L);
			userService.changeFriends(FriendsAction.REQUEST, 2L, 1L);

			User user1 = userService.get(1L);
			User user2 = userService.get(2L);

			assertTrue(user1.getFriendsIds().contains(2L));
			assertTrue(user1.getRejectedRequests().isEmpty());
			assertTrue(user1.getIncomingFriendRequests().isEmpty());
			assertTrue(user1.getOutgoingFriendRequests().isEmpty());

			assertTrue(user2.getFriendsIds().contains(1L));
			assertTrue(user2.getRejectedRequests().isEmpty());
			assertTrue(user2.getIncomingFriendRequests().isEmpty());
			assertTrue(user2.getOutgoingFriendRequests().isEmpty());

		}

		@Test
		void approveFriendsWithoutRequest() {
			userService.create(User.builder()
					.login("User2")
					.email("user2@mail.ru")
					.birthday(LocalDate.parse("2020-10-20"))
					.build()
			);

			NotFoundException exception = assertThrows(NotFoundException.class,
					() -> userService.changeFriends(FriendsAction.APPROVE, 2L, 1L));

			assertEquals("Невозможно одобрить. " +
							"Пользователь с id=1 не отправлял запрос в друзья к пользователю с id=2.",
					exception.getMessage()
			);

			User user1 = userService.get(1L);
			User user2 = userService.get(2L);

			assertTrue(user1.getFriendsIds().isEmpty());
			assertTrue(user1.getRejectedRequests().isEmpty());
			assertTrue(user1.getIncomingFriendRequests().isEmpty());
			assertTrue(user1.getOutgoingFriendRequests().isEmpty());

			assertTrue(user2.getFriendsIds().isEmpty());
			assertTrue(user2.getRejectedRequests().isEmpty());
			assertTrue(user2.getIncomingFriendRequests().isEmpty());
			assertTrue(user2.getOutgoingFriendRequests().isEmpty());

		}

		@Test
		void getMutualFriends() {
			userService.create(User.builder()
					.login("User2")
					.email("user2@mail.ru")
					.birthday(LocalDate.parse("2020-10-20"))
					.build()
			);

			userService.create(User.builder()
					.login("User3")
					.email("user3@mail.ru")
					.birthday(LocalDate.parse("2020-10-20"))
					.build()
			);

			userService.changeFriends(FriendsAction.REQUEST, 1L, 2L);
			userService.changeFriends(FriendsAction.APPROVE, 2L, 1L);

			userService.changeFriends(FriendsAction.REQUEST, 2L, 3L);
			userService.changeFriends(FriendsAction.APPROVE, 3L, 2L);

			userService.changeFriends(FriendsAction.REQUEST, 3L, 1L);
			userService.changeFriends(FriendsAction.APPROVE, 1L, 3L);

			Collection<User> mutuals = userService.getListOfMutualFriends(1L, 3L);

			assertEquals(1, mutuals.size());
			assertTrue(mutuals.contains(userService.get(2L)));
		}

		@Test
		void getIncomingFriends() {
			userService.create(User.builder()
					.login("User2")
					.email("user2@mail.ru")
					.birthday(LocalDate.parse("2020-10-20"))
					.build()
			);

			userService.create(User.builder()
					.login("User3")
					.email("user3@mail.ru")
					.birthday(LocalDate.parse("2020-10-20"))
					.build()
			);

			userService.changeFriends(FriendsAction.REQUEST, 1L, 2L);
			userService.changeFriends(FriendsAction.REQUEST, 3L, 2L);

			Collection<User> incoming = userService.getIncomingFriends(2L);

			assertEquals(2, incoming.size());
			assertTrue(incoming.contains(userService.get(1L)));
			assertTrue(incoming.contains(userService.get(3L)));
		}

		@Test
		void getOutgoingFriends() {
			userService.create(User.builder()
					.login("User2")
					.email("user2@mail.ru")
					.birthday(LocalDate.parse("2020-10-20"))
					.build()
			);

			userService.create(User.builder()
					.login("User3")
					.email("user3@mail.ru")
					.birthday(LocalDate.parse("2020-10-20"))
					.build()
			);

			userService.changeFriends(FriendsAction.REQUEST, 2L, 1L);
			userService.changeFriends(FriendsAction.REQUEST, 2L, 3L);

			Collection<User> outgoing = userService.getOutgoingFriends(2L);

			assertEquals(2, outgoing.size());
			assertTrue(outgoing.contains(userService.get(1L)));
			assertTrue(outgoing.contains(userService.get(3L)));
		}

	}

}