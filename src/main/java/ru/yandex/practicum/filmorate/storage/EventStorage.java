package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.mapper.EventRowMapper;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventStorage {
    private final JdbcTemplate jdbc;
    private final EventRowMapper eventRowMapper;

    private static final String INSERT_EVENT_QUERY = """
                INSERT INTO EVENTS (id, user_id, event_type, operation, timestamp, entity_id)
                VALUES (?, ?,  ?,  ?, ?, ?)
            """;

    private static final String GET_EVENTS_BY_USER_QUERY = """
                SELECT id, timestamp, user_id, event_type, operation, entity_id
                FROM events
                WHERE user_id = ?
                ORDER BY timestamp ASC
            """;

    private static final String GET_ALL_EVENTS_QUERY = """
                SELECT id, timestamp, user_id, event_type, operation, entity_id
                FROM events
                ORDER BY timestamp ASC
            """;

    public void addEvent(Event event) {
        jdbc.update(
                INSERT_EVENT_QUERY,
                event.getEventId(),
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getTimestamp(),
                event.getEntityId()
        );
    }

    public List<Event> getEventsByUserId(Long userId) {
        return jdbc.query(GET_EVENTS_BY_USER_QUERY, eventRowMapper, userId);
    }

    public List<Event> getAllEvents() {
        return jdbc.query(GET_ALL_EVENTS_QUERY, eventRowMapper);
    }
}