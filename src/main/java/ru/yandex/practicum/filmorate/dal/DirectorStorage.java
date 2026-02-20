package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {

	/// Получить режиссера по id
	Optional<Director> findById(long id);

	/// Получить всех режиссеров
	Collection<Director> findAll();

	/// Добавить нового режиссера
	Director createDirector(Director director);

	/// Обновить данные режиссера
	Director updateDirector(Director director);

	/// Удалить режиссера по id
	void deleteDirector(Integer directorId);

	/// Проверить что директор отсутствует по id
	boolean checkDirectorIsNotPresent(int directorId);

}
