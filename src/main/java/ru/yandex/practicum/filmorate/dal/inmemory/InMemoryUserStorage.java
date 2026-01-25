package ru.yandex.practicum.filmorate.dal.inmemory;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.UserStorage;
import ru.yandex.practicum.filmorate.exception.MethodNotImplementedException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.servise.util.IdentifyService;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Component
@Qualifier("inMemoryUserStorage")
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

	private final Map<Long, User> users;
	private final IdentifyService identifyService;

	@Override
	public Optional<User> findById(long id) {
		return users.values().stream().filter(u -> u.getId().equals(id)).findFirst();
	}

	@Override
	public Collection<User> findAll() {
		return users.values();
	}

	@Override
	public User createUser(User user) {
		user.setId(identifyService.getNextId(users));
		users.put(user.getId(), user);
		return user;
	}

	@Override
	public boolean checkUserIsNotPresent(Long userId) {
		return users.containsKey(userId);
	}

	@Override
	public User updateUser(User user) {
		users.put(user.getId(), user);
		return user;
	}

	@Override
	public void addFriend(long userId, long friendId) {
		throw new MethodNotImplementedException();
	}

	@Override
	public void removeFriend(long userId, long friendId) {
		throw new MethodNotImplementedException();
	}

	@Override
	public Collection<User> findFriendsOfUser(long userId) {
		throw new MethodNotImplementedException();
	}

	@Override
	public Collection<User> findMutualFriends(long userId, long friendId) {
		throw new MethodNotImplementedException();
	}

	@Override
	public Collection<Long> getFriendIds(long userId) {
		throw new MethodNotImplementedException();
	}

}
