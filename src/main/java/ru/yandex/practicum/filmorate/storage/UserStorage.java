package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

	/// Получить пользователя по id
	Optional<User> get(Long id);

	/// Получить всех пользователей
	Collection<User> findAll();

	/// Добавить нового пользователя
	User create(User user);

	/// Обновить данные пользователя
	void update(User user);

	/// Получение id всех пользователей
	Collection<Long> getUserIds();

	/// Сброс данных
	void reset();
}
