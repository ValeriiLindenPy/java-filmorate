package ru.yandex.practicum.filmorate.service.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.LikeNotExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {
    private final LikeStorage likeStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventService eventService;

    /**
     * Adds a like from a user to a film.
     *
     * @param filmId
     * @param userId
     * @throws NotFoundException
     */
    public void addLike(long filmId, long userId) {
        filmStorage.getById(filmId).orElseThrow(() -> new NotFoundException("Film not found"));
        userStorage.getById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        likeStorage.addLike(userId, filmId);
        eventService.createEvent(
                userId,
                EventType.LIKE,
                OperationType.ADD,
                filmId
        );
        log.debug("User with ID {} liked the film with ID {}", userId, filmId);
    }

    /**
     * Removes a like from a user to a film.
     *
     * @param filmId
     * @param userId
     * @throws NotFoundException
     */
    public void removeLike(long filmId, long userId) {
        filmStorage.getById(filmId).orElseThrow(() -> new NotFoundException("Film not found"));
        userStorage.getById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        if (!likeStorage.getFilmLikes(filmId).contains(userId)) {
            throw new LikeNotExistException("User did not like this film");
        }
        eventService.createEvent(
                userId,
                EventType.LIKE,
                OperationType.REMOVE,
                filmId
        );
        likeStorage.removeLike(userId, filmId);
        log.debug("User with ID {} removed the like from the film with ID {}", userId, filmId);

    }
}
