package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

public interface EventStorage {

    /// Получение всех действий пользователя по его id
    Collection<Event> getAllEventsByUserId(long userId);

    /// Добавление события
    Event addEvent(Event event);
}