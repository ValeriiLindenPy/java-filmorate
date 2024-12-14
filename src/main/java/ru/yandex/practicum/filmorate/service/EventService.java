package ru.yandex.practicum.filmorate.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {
    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    /**
     * Adds a new event to the storage.
     *
     * @param event the event object to be saved.
     */
    public void addEvent(Event event) {
        long id = generateId();
        event.setEventId(id);
        eventStorage.addEvent(event);
        log.info("Event successfully added: {}", id);
    }

    /**
     * Retrieves a list of events for a specific user.
     *
     * @param userId the ID of the user.
     * @return a list of events associated with the user.
     */
    public List<Event> getEventsByUserId(Long userId) {
        log.trace("Checking existence of user with ID {}", userId);
        // Проверка, существует ли пользователь с заданным userId
        if (userStorage.getById(userId).isEmpty()) {
            log.warn("User with ID {} does not exist", userId);
            throw new NotFoundException("User with ID %s does not exist".formatted(userId));
        }
        log.trace("Retrieves a list of events for a user {}", userId);
        return eventStorage.getEventsByUserId(userId);
    }


    /**
     * Utility method to create and save an event.
     *
     * @param userId    the ID of the user.
     * @param eventType the type of the event.
     * @param operation the operation of the event.
     * @param entityId  the ID of the entity related to the event.
     */
    public void createEvent(Long userId, EventType eventType, OperationType operation, Long entityId) {
        Event event = Event.builder()
                .userId(userId)
                .eventType(eventType)
                .operation(operation)
                .timestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) * 1000)
                .entityId(entityId)
                .build();
        addEvent(event);
    }

    /**
     * Generates a new unique ID for a film.
     *
     * @return {@link Long}
     */
    private Long generateId() {
        Long currentId = eventStorage.getAllEvents().stream()
                .mapToLong(Event::getEventId)
                .max()
                .orElse(0);
        return ++currentId;
    }
}
