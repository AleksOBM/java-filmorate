package ru.yandex.practicum.filmorate.dal.rowmappers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Qualifier("eventRowMapper")
public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Event.builder()
                .id(resultSet.getLong("id"))
                .userId(resultSet.getLong("user_id"))
                .timestamp(resultSet.getTimestamp("created_at").toInstant())
                .eventType(EventType.valueOf(resultSet.getString("event_type")))
                .operation(Operation.valueOf(resultSet.getString("operation")))
                .entityId(resultSet.getLong("entity_id"))
                .build();
    }
}