package ru.yandex.practicum.filmorate.servise;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UserStorage userStorage;

	public void changeFriends(FriendsAction action, Long userId1, Long userId2) {
		switch (action) {
			case ADD -> log.info("Добавление в друзья пользователей id={} и id={}.", userId1, userId2);
			case REMOVE -> log.trace("Удаление из друзей пользователей id={} и id={}.", userId1, userId2);
		}

		User[] users = validateTwoUsers(userId1, userId2);

		switch (action) {
			case ADD -> {
				users[0].addFriendId(userId2);
				users[1].addFriendId(userId1);
			}
			case REMOVE -> {
				users[0].removeFriends(userId2);
				users[1].removeFriends(userId1);
			}
		}
	}

	public Collection<User> getListOfFriends(Long userId) {
		log.info("Получение списка друзей пользователя id={}.", userId);
		User user = userStorage.get(userId).orElseThrow(() ->
				new NotFoundException("Пользователь с id=" + userId + " не найден.")
		);

		return user.getFriendsIds().stream()
				.sorted(Long::compareTo)
				.map(userStorage::get)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.toList();
	}

	public Collection<User> getListOfMutualFriends(Long userId1, Long userId2) {
		log.info("Получение списка общих друзей у пользователей id={} и id={}.", userId1, userId2);

		User[] users = validateTwoUsers(userId1, userId2);

		Collection<Long> mutualFriendTds = users[0].getFriendsIds().stream()
				.filter(users[1].getFriendsIds()::contains)
				.toList();

		return mutualFriendTds.stream()
				.sorted(Long::compareTo)
				.map(userStorage::get)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.toList();
	}

	public Collection<User> findAll() {
		log.info("Поиск всех пользователей");
		return userStorage.findAll();
	}

	public User get(Long id) {
		return userStorage.get(id).orElseThrow(() ->
				new NoContentException("Пользователь с id=" + id + " не найден.")
		);
	}

	public User create(User user) {
		log.info("Добавления нового пользователя:");
		userValidate(user);

		log.trace("Проверка логина нового пользователя");
		if (user.getLogin() == null) {
			throw new ValidationException("Логин должен быть указан.");
		}

		log.trace("Проверка даты электронной почты нового пользователя");
		if (user.getEmail() == null) {
			throw new ValidationException("Электронная почта должна быть указана.");
		}

		log.trace("Обработка имени нового пользователя");
		if ((user.getName() == null) || (user.getName().isBlank())) {
			user.setName(user.getLogin());
			log.trace("Пользователю присвоено имя из логина.");
		}

		log.trace("Проверка даты рождения нового пользователя");
		if (user.getBirthday() == null) {
			throw new ValidationException("Дата рождения должна быть указана.");
		}

		log.trace("Сохранение нового пользователя");
		return userStorage.create(user);
	}

	public User update(User user) {
		log.info("Обновление данных о пользователе:");

		log.trace("Проверка id пользователя");
		if (user.getId() == null) {
			throw new ConditionsNotMetException("Id должен быть указан.");
		} else if (!userStorage.getUserIds().contains(user.getId())) {
			throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден.");
		}

		userValidate(user);

		log.trace("Получение текущих данных о пользователе");
		User oldUser = userStorage.get(user.getId()).orElseThrow();

		log.trace("Обработка логина");
		if (user.getLogin() == null) {
			user.setLogin(oldUser.getLogin());
		} else {
			log.info("Логин пользователя изменен.");
		}

		log.trace("Обработка имени пользователя");
		if (user.getName() == null) {
			user.setName(oldUser.getName());
		} else if ((user.getName().isBlank())) {
			user.setName(user.getLogin());
		} else {
			log.info("Имя пользователя изменено.");
		}

		log.trace("Обработка электронной почты");
		if (user.getEmail() == null) {
			user.setEmail(oldUser.getEmail());
		} else {
			log.info("Электронная почта изменена.");
		}

		log.trace("Обработка даты рождения");
		if (user.getBirthday() == null) {
			user.setBirthday(oldUser.getBirthday());
		} else {
			log.info("Дата рождения изменена.");
		}

		log.trace("Сохранение новых данных пользователя");
		userStorage.update(user);

		return user;
	}

	private User[] validateTwoUsers(Long userId1, Long userId2) {
		if (userId1.equals(userId2)) {
			throw new ParameterNotValidException(userId1.toString(), "У пользователей должны быть разные id.");
		}

		User user1 = userStorage.get(userId1).orElseThrow(() ->
				new NotFoundException("Первый пользователь с id=" + userId1 + " не найден.")
		);

		User user2 = userStorage.get(userId2).orElseThrow(() ->
				new NotFoundException("Второй пользователь с id=" + userId2 + " не найден.")
		);

		return new User[]{user1, user2};
	}

	private void userValidate(User user) {

		log.trace("Проверка электронной почты");
		if ((user.getEmail() != null) && (user.getEmail().isBlank())) {
			throw new ConditionsNotMetException("Электронная почта не может быть пустой.");
		} else if ((user.getEmail() != null) && !(user.getEmail().contains("@"))) {
			throw new ParameterNotValidException(user.getEmail(),
					"Электронная почта должна содержать символ '@'."
			);
		} else if ((user.getEmail() != null) &&
				userStorage.findAll().stream().map(User::getEmail).anyMatch(u -> u.equals(user.getEmail()))
		) {
			throw new DuplicatedDataException("Эта электронная почта " + user.getEmail() + " уже используется.");
		}

		log.trace("Проверка логина");
		if ((user.getLogin() != null) && (user.getLogin().isBlank())) {
			throw new ConditionsNotMetException("Логин не может быть пустым.");
		} else if ((user.getLogin() != null) && (user.getLogin().contains(" "))) {
			throw new ParameterNotValidException(user.getLogin(),
					"Логин не должен содержать пробелов."
			);
		}

		log.trace("Проверка даты рождения");
		if ((user.getBirthday() != null) && (user.getBirthday().isAfter(LocalDate.now()))) {
			throw new ParameterNotValidException(user.getBirthday().toString(),
					"Дата рождения не может быть в будущем."
			);
		}
	}
}
