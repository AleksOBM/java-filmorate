package ru.yandex.practicum.filmorate.servise;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UserStorage userStorage;

	public void changeFriends(FriendsAction action, Long userId, Long friendId) {
		switch (action) {
			case REQUEST -> log.info(
					"Запрос в друзья от пользователя id={} к пользователю id={}.", userId, friendId
			);
			case APPROVE -> log.info(
					"Одобрение зароса в друзья пользователем id={} пользователю id={}.", userId, friendId
			);
			case REJECT -> log.info(
					"Отклонение зароса в друзья пользователем id={} пользователю id={}.", userId, friendId
			);
			case REMOVE -> log.trace(
					"Удаление из друзей пользователя id={} одного пользователя id={}.", userId, friendId
			);
		}

		User[] users = validateTwoUsers(userId, friendId);

		switch (action) {
			case REQUEST -> {
				if (users[0].getIncomingFriendRequests().contains(friendId)) {
					users[0].addFriendId(friendId);
					users[1].addFriendId(userId);
					users[0].removeIncomingFriendRequest(friendId);
					users[1].removeOutgoingFriendRequest(userId);
					log.info("Пользователи с id={} и id={} стали друзьями, по взаимному запросу.", userId, friendId);
					break;
				}
				if (users[0].getFriendsIds().contains(friendId)) {
					throw new AlredyAcceptedException("Пользователь с id=" + friendId +
							" уже в друзьях у пользователя с id=" + userId + "."
					);
				}
				if (users[1].getRejectedRequests().contains(userId)) {
					throw new NotAllowedException("Пользователь с id=" + friendId +
							" уже отклонил запрос пользователя с id=" + userId + " ранее."
					);
				}
				if (users[0].getOutgoingFriendRequests().contains(friendId)) {
					throw new AlredyAcceptedException("Пользователь с id=" + userId +
							" уже в отправлял запрос пользователю с id=" + friendId + "."
					);
				}

				users[0].removeRejectResponse(friendId);
				users[0].addOutgoingFriendRequest(friendId);
				users[1].addIncomingFriendRequest(userId);
				log.info("Пользователь с id={} отправил запрос в друзья к пользователю с id={}.", userId, friendId);
			}
			case APPROVE -> {
				if (!users[0].getIncomingFriendRequests().contains(friendId)) {
					throw new NotFoundException(
							"Невозможно одобрить. Пользователь с id=" + friendId +
									" не отправлял запрос в друзья к пользователю с id=" + userId + "."
					);
				}
				users[0].removeIncomingFriendRequest(friendId);
				users[1].removeOutgoingFriendRequest(userId);
				users[0].addFriendId(friendId);
				users[1].addFriendId(userId);
				log.info("Пользователь с id={} одобрил запрос в друзья от пользователя с id={}.", userId, friendId);
			}
			case REJECT -> {
				if (!users[0].getIncomingFriendRequests().contains(friendId)) {
					throw new NotFoundException(
							"Невозможно отклонить. Пользователь с id=" + friendId +
									" не отправлял запрос в друзья к пользователю с id=" + userId + "."
					);
				}
				users[0].removeIncomingFriendRequest(friendId);
				users[1].removeOutgoingFriendRequest(userId);
				users[0].addRejectResponse(friendId);
				log.info("Пользователь с id={} отклонил запрос в друзья от пользователя с id={}.", userId, friendId);
			}
			case REMOVE -> {
				if (!users[0].getFriendsIds().contains(friendId)) {
					throw new NotFoundException(
							"Невозможно удалить. Пользователь с id=" + friendId +
									" не находится в друзьях у пользователя с id=" + userId + "."
					);
				}
				users[0].removeFriends(friendId);
				users[1].removeFriends(userId);
				log.info("Пользователь с id={} удалил из друзей пользователя с id={}.", userId, friendId);
			}
		}
	}

	public Collection<User> getIncomingFriends(Long userId) {
		User u = userStorage.get(userId).orElseThrow(() ->
				new NotFoundException("Первый пользователь с id=" + userId + " не найден.")
		);

		return u.getIncomingFriendRequests().stream()
				.sorted(Long::compareTo)
				.map(userStorage::get)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.toList();
	}

	public Collection<User> getOutgoingFriends(Long userId) {
		User u = userStorage.get(userId).orElseThrow(() ->
				new NotFoundException("Первый пользователь с id=" + userId + " не найден.")
		);

		return u.getOutgoingFriendRequests().stream()
				.sorted(Long::compareTo)
				.map(userStorage::get)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.toList();
	}

	public Collection<User> getRejectedFriends(Long userId) {
		User u = userStorage.get(userId).orElseThrow(() ->
				new NotFoundException("Первый пользователь с id=" + userId + " не найден.")
		);

		return u.getRejectedRequests().stream()
				.sorted(Long::compareTo)
				.map(userStorage::get)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.toList();
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
		log.info("Добавление нового пользователя");
		userValidate(user);

		if (user.getLogin() == null) {
			throw new ValidationException("Логин должен быть указан.");
		}

		if (user.getEmail() == null) {
			throw new ValidationException("Электронная почта должна быть указана.");
		}

		if ((user.getName() == null) || (user.getName().isBlank())) {
			user.setName(user.getLogin());
			log.trace("Пользователю присвоено имя из логина.");
		}

		if (user.getBirthday() == null) {
			throw new ValidationException("Дата рождения должна быть указана.");
		}

		return userStorage.create(user);
	}

	public User update(User user) {
		log.info("Обновление данных о пользователе");

		if (user.getId() == null) {
			throw new ConditionsNotMetException("Id должен быть указан.");
		} else if (!userStorage.getUserIds().contains(user.getId())) {
			throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден.");
		}

		userValidate(user);

		User oldUser = userStorage.get(user.getId()).orElseThrow();

		if (user.getLogin() == null) {
			user.setLogin(oldUser.getLogin());
		} else {
			log.info("Логин пользователя изменен.");
		}

		if (user.getName() == null) {
			user.setName(oldUser.getName());
		} else if ((user.getName().isBlank())) {
			user.setName(user.getLogin());
		} else {
			log.info("Имя пользователя изменено.");
		}

		if (user.getEmail() == null) {
			user.setEmail(oldUser.getEmail());
		} else {
			log.info("Электронная почта изменена.");
		}

		if (user.getBirthday() == null) {
			user.setBirthday(oldUser.getBirthday());
		} else {
			log.info("Дата рождения изменена.");
		}

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

		if ((user.getEmail() != null) &&
				userStorage.findAll().stream()
						.filter(u -> !(u.getId().equals(user.getId())))
						.map(User::getEmail).anyMatch(u -> u.equals(user.getEmail()))
		) {
			throw new DuplicatedDataException("Эта электронная почта " + user.getEmail() + " уже используется.");
		}

	}

}
