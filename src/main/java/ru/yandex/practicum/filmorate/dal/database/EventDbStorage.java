package ru.yandex.practicum.filmorate.dal.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.EventStorage;
import ru.yandex.practicum.filmorate.dal.rowmappers.EventRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

@Repository
@Qualifier("eventDbStorage")
public class EventDbStorage extends BaseDbStorage<Event> implements EventStorage {
    private static final String GET_USER_FEEDS =
            "SELECT * FROM events " +
                    "WHERE user_id = ? " +
                    "ORDER BY created_at;";

    private static final String ADD_USER_EVENT =
            "INSERT INTO events (user_id, created_at, event_type, operation, entity_id) VALUES (?, ?, ?, ?, ?);";

    @Autowired
    public EventDbStorage(JdbcTemplate jdbc, EventRowMapper rowMapper) {
        super(jdbc, rowMapper);
    }

    @Override
    public Collection<Event> getAllEventsByUserId(long userId) {
        return findManyByQuery(GET_USER_FEEDS, userId);
    }

    public Event addEvent(Event event) {
        if (event == null) {
            throw new NotFoundException("Событие для добавления не найдено");
        }

        long eventId = insertWithKeyHolder(ADD_USER_EVENT,
                event.getUserId(),
                event.getTimestamp(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId());
        event.setId(eventId);
        return event;
    }
}