package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.servise.IdentifyService;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

	private final Map<Long, User> users;
	private final IdentifyService identifyService;

	@Override
	public Optional<User> get(Long id) {
		return users.values().stream().filter(u -> u.getId().equals(id)).findFirst();
	}

	@Override
	public Collection<User> findAll() {
		return users.values();
	}

	@Override
	public User create(User user) {
		user.setId(identifyService.getNextId(users));
		users.put(user.getId(), user);
		return user;
	}

	@Override
	public void update(User user) {
		users.put(user.getId(), user);
	}

	public Collection<Long> getUserIds() {
		return users.keySet();
	}

	@Override
	public void reset() {
		users.clear();
	}
}
