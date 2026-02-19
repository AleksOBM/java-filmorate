package ru.yandex.practicum.filmorate.servise;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.EventStorage;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventService {
    @Autowired
    @Qualifier("eventDbStorage")
    private EventStorage eventStorage;

    public Event addUserEvent(long userId, EventType eventType, Operation operation, long entityId) {
        Event event = Event.builder()
                .userId(userId)
                .timestamp(Instant.now())
                .eventType(eventType)
                .operation(operation)
                .entityId(entityId)
                .build();
        return eventStorage.addEvent(event);
    }

    public Collection<EventDto> getUserEvents(long userId) {
        log.info("Поиск всех событий пользователя.");
        return eventStorage.getAllEventsByUserId(userId)
                .stream()
                .map(EventMapper::mapToEventDto)
                .collect(Collectors.toList());
    }
}
