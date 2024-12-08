package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.mapper.EventRowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventStorage {
    private final JdbcTemplate jdbc;
    private final EventRowMapper eventRowMapper;

    private static final String INSERT_EVENT_QUERY = """
    INSERT INTO EVENTS (id, user_id, event_type, operation, timestamp, entity_id)
    VALUES (?, ?, (SELECT event_type FROM event_types WHERE name = ?), (SELECT operation FROM operations WHERE name = ?), ?, ?)
""";

    private static final String GET_EVENTS_BY_USER_QUERY = """
    SELECT e.id, e.user_id, et.name AS event_type, o.name AS operation, e.timestamp, e.entity_id
    FROM EVENTS e
    JOIN event_types et ON e.event_type = et.event_type
    JOIN operations o ON e.operation = o.operation
    WHERE e.user_id = ?
    ORDER BY e.timestamp ASC
""";

    private static final String GET_ALL_EVENTS_QUERY = """
    SELECT e.id, e.timestamp, e.user_id, et.name AS event_type, o.name AS operation, e.entity_id
    FROM EVENTS e
    JOIN event_types et ON e.event_type = et.event_type
    JOIN operations o ON e.operation = o.operation
    ORDER BY e.timestamp ASC
""";

    public void addEvent(Event event) {
        //System.out.println(event.getTimestamp().toEpochSecond(ZoneOffset.UTC));
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