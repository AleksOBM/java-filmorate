package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmStorage;
import ru.yandex.practicum.filmorate.dal.UserStorage;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.dto.request.create.UserCreateRequest;
import ru.yandex.practicum.filmorate.dto.request.update.UserUpdateRequest;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.util.FriendsAction;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

	@Autowired
	@Qualifier("userDbStorage")
	private UserStorage userStorage;

	@Autowired
	@Qualifier("filmDbStorage")
	private FilmStorage filmStorage;

	@Autowired
	private EventService eventService;

	@Autowired
	private FilmService filmService;

	public void changeFriends(FriendsAction action, long userId, long friendId) {
		switch (action) {
			case REQUEST -> log.info(
					"Запрос в друзья от пользователя id={} к пользователю id={}.", userId, friendId
			);
			case REMOVE -> log.trace(
					"Удаление из друзей пользователя id={} одного пользователя id={}.", userId, friendId
			);
		}

		validateTwoUsers(userId, friendId);
		Collection<Long> friendsOfUser = userStorage.getFriendIds(userId);
		Collection<Long> riendsOfFriend = userStorage.getFriendIds(friendId);

		switch (action) {
			case REQUEST -> {
				if (friendsOfUser.contains(friendId) && riendsOfFriend.contains(userId)) {
					throw new AlredyAcceptedException("Пользователи с id=" + userId +
							" и id=" + friendId + " уже друзья."
					);
				}
				if (friendsOfUser.contains(friendId)) {
					throw new AlredyAcceptedException("Пользователь с id=" + userId +
							" уже отправлял запрос в друзья пользователю с id=" + friendId + "."
					);
				}

				if (riendsOfFriend.contains(userId)) {
					log.info("Пользователи с id={} и id={} стали друзьями.", userId, friendId);
				}

				userStorage.addFriend(userId, friendId);
				eventService.addUserEvent(userId, EventType.FRIEND, Operation.ADD, friendId);
			}

			case REMOVE -> {
				if (!friendsOfUser.contains(friendId)) {
					throw new NoContentException();
				}
				userStorage.removeFriend(userId, friendId);
				eventService.addUserEvent(userId, EventType.FRIEND, Operation.REMOVE, friendId);
			}
		}
	}

	public UserDto create(UserCreateRequest request) {
		log.info("Добавление нового пользователя login={}.", request.getLogin());
		if (request.getName().isBlank()) {
			request.setName(request.getLogin());
		}

		User user = UserMapper.mapToUser(request);

		try {
			user = userStorage.createUser(user);
		} catch (DuplicateKeyException e) {
			throw new DuplicatedDataException("Эта электронная почта " + user.getEmail() + " уже используется.");
		}

		return UserMapper.mapToUserDto(user);
	}

	public UserDto update(UserUpdateRequest request) {
		log.info("Обновление данных о пользователе с id={}.", request.getId());
		if (request.getName().isBlank()) {
			request.setName(request.getLogin());
		}

		User oldUser = findUser(request.getId());
		logUserUpdate(request, oldUser);
		User newUser = UserMapper.updateUserFields(oldUser, request);

		try {
			newUser = userStorage.updateUser(newUser);
		} catch (DuplicateKeyException e) {
			throw new DuplicatedDataException(
					"Эта электронная почта " + newUser.getEmail() + " уже используется."
			);
		}

		return UserMapper.mapToUserDto(newUser);
	}

	public void remove(long userId) {
		log.info("Удаление пользователя с id={}.", userId);
		easyCheckUser(userId);
		userStorage.removeUser(userId);
	}

	public UserDto findUserDtoById(long userId) {
		log.info("Поиск пользователя с id={}.", userId);
		return UserMapper.mapToUserDto(findUser(userId));
	}

	public Collection<UserDto> findAllUserDto() {
		log.info("Поиск всех пользователей.");
		return userStorage.findAll()
				.stream()
				.map(UserMapper::mapToUserDto)
				.collect(Collectors.toList());
	}

	public Collection<UserDto> getListOfFriends(long userId) {
		log.info("Получение списка друзей пользователя id={}.", userId);
		easyCheckUser(userId);

		return userStorage.findFriendsOfUser(userId).stream()
				.map(UserMapper::mapToUserDto)
				.toList();
	}

	public Collection<UserDto> getListOfMutualFriends(long userId, long friendId) {
		log.info("Получение списка общих друзей у пользователей id={} и id={}.", userId, friendId);
		validateTwoUsers(userId, friendId);

		return userStorage.findMutualFriends(userId, friendId).stream()
				.map(UserMapper::mapToUserDto)
				.toList();
	}

	public Collection<EventDto> getUserEvents(long userId) {
		log.info("Получение событий пользователя с id={}.", userId);
		easyCheckUser(userId);
		return eventService.getUserEvents(userId);
	}

	private void easyCheckUser(long userId) {
		if (userStorage.checkUserIsNotPresent(userId)) {
			throw new NotFoundException("Первый пользователь с id=" + userId + " не найден.");
		}
	}

	private User findUser(long userId) {
		return userStorage.findById(userId)
				.orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден."));
	}

	private void validateTwoUsers(long userId1, long userId2) {
		if (userId1 == userId2) {
			throw new ParameterNotValidException(String.valueOf(userId1), "У пользователей должны быть разные id.");
		}

		easyCheckUser(userId1);
		easyCheckUser(userId2);
	}

	private void logUserUpdate(UserUpdateRequest request, User user) {
		List<String> updatedFields = new ArrayList<>();

		if (request.hasNname() && !request.getName().equals(user.getName())) {
			updatedFields.add("имя");
		}
		if (request.hasEmail() && !request.getEmail().equals(user.getEmail())) {
			updatedFields.add("электронная почта");
		}
		if (request.hasLogin() && !request.getLogin().equals(user.getLogin())) {
			updatedFields.add("логин");
		}
		if (request.hasBirthday() && !request.getBirthday().isEqual(user.getBirthday())) {
			updatedFields.add("дата рождения");
		}

		String updatedData = String.join(", ", updatedFields);
		log.info("Данные для обновления: [{}]", updatedData);
	}

	public Collection<FilmDto> getRecommendations(long userId) {
		log.info("Получение рекомендаций для пользователя id={}.", userId);
		easyCheckUser(userId);

		Map<Long, Set<Long>> allLikes = filmStorage.getAllLikes();
		Set<Long> targetUserLikes = allLikes.getOrDefault(userId, Collections.emptySet());

		if (targetUserLikes.isEmpty()) {
			return Collections.emptyList();
		}

		Map<Long, Integer> similarityMap = new HashMap<>();

		for (Map.Entry<Long, Set<Long>> entry : allLikes.entrySet()) {
			long otherUserId = entry.getKey();
			if (otherUserId == userId) continue;

			Set<Long> otherUserLikes = entry.getValue();
			int overlap = (int) targetUserLikes.stream()
					.filter(otherUserLikes::contains)
					.count();

			if (overlap > 0) {
				similarityMap.put(otherUserId, overlap);
			}
		}

		if (similarityMap.isEmpty()) {
			return Collections.emptyList();
		}

		List<Long> similarUserIds = similarityMap.entrySet().stream()
				.sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
				.limit(15)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());

		Set<Long> recommendedFilmIds = new HashSet<>();
		for (Long simId : similarUserIds) {
			Set<Long> likedBySimilarUser = allLikes.get(simId);
			likedBySimilarUser.stream()
					.filter(filmId -> !targetUserLikes.contains(filmId))
					.forEach(recommendedFilmIds::add);
		}

		if (recommendedFilmIds.isEmpty()) {
			return Collections.emptyList();
		}

		return filmService.getFilmsByIds(recommendedFilmIds);
	}
}
