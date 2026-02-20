package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

	/// Получить пользователя по id
	Optional<User> findById(long id);

	/// Получить всех пользователей
	Collection<User> findAll();

	/// Добавить нового пользователя
	User createUser(User user);

	/// Проверить существование пользователя в базе
	boolean checkUserIsNotPresent(Long userId);

	/// Обновить данные пользователя
	User updateUser(User user);

	/// Добавить в друзья
	void addFriend(long userId, long friendId);

	/// Удалить из друзей
	void removeFriend(long userId, long friendId);

	/// Получить всех друзей пользователя по id
	Collection<User> findFriendsOfUser(long userId);

	/// Получить общих друзей двух пользователей по id
	Collection<User> findMutualFriends(long userId, long friendId);

	/// Получить список id друзей
	Collection<Long> getFriendIds(long userId);

	/// Удаление пользователя по id
	void removeUser(long userId);
}
