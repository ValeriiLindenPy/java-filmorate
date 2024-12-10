package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        //System.out.println(Instant.ofEpochMilli(rs.getLong("timestamp")).atZone(ZoneOffset.UTC).toLocalDateTime());
        return Event.builder()
                .eventId(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .entityId(rs.getLong("entity_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(OperationType.valueOf(rs.getString("operation")))
                .timestamp(rs.getLong("timestamp"))
                .build();
    }
}
