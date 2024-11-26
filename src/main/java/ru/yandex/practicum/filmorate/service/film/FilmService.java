package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MPAStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final MPAStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final UserStorage userStorage;
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    /**
     * Retrieves all films.
     *
     * @return {@link Collection<Film>}
     */
    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    /**
     * Retrieves a film by its ID.
     *
     * @param id
     * @return {@link Film}
     * @throws NotFoundException
     */
    public Film getById(long id) {
        return filmStorage.getById(id).orElseThrow(() ->
                new NotFoundException("Film with id %d not found".formatted(id)));
    }

    /**
     * Creates a new film.
     *
     * @param film
     * @return {@link Film}
     * @throws ValidationException
     */
    public Film create(Film film) {
        log.trace("Check release date");
        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            log.warn("The date {} is before the minimum allowed date {}", film.getReleaseDate(), MIN_DATE);
            throw new ValidationException("Release date must not be earlier than December 28, 1895");
        }

        // Validate MPA rating
        if (film.getMpa() != null && mpaStorage.getById(film.getMpa().getId()).isEmpty()) {
            throw new ValidationException("Invalid MPA ID: " + film.getMpa().getId());
        }

        // Validate genres
        if (!film.getGenres().isEmpty()) {
            boolean genreValid = film.getGenres().stream()
                    .noneMatch(genre -> genreStorage.getById(genre.getId()).isEmpty());
            if (!genreValid) {
                throw new ValidationException("Invalid genre of film!");
            }
        }

        log.trace("Set film ID");
        film.setId(generateId());
        log.trace("Add film to storage");
        return filmStorage.create(film);
    }

    /**
     * Updates an existing film.
     *
     * @param newFilm
     * @return {@link Film}
     * @throws NotFoundException
     */
    public Film update(Film newFilm) {
        log.debug("Check if film with ID {} exists", newFilm.getId());
        if (filmStorage.getById(newFilm.getId()).isPresent()) {
            log.trace("Update film in storage");
            return filmStorage.update(newFilm);
        }
        log.warn("Film with ID {} is not found", newFilm.getId());
        throw new NotFoundException("Film not found!");
    }

    /**
     * Retrieves the top-rated films.
     *
     * @param count - int
     * @return {@link Collection<Film>}
     */
    public Collection<Film> getTop(int count) {
        return filmStorage.getTop(count);
    }

    /**
     * Adds a like from a user to a film.
     *
     * @param filmId
     * @param userId
     * @throws NotFoundException
     */
    public void addLike(long filmId, long userId) {
        Optional<Film> film = filmStorage.getById(filmId);
        Optional<User> user = userStorage.getById(userId);

        if (film.isEmpty() || user.isEmpty()) {
            throw new NotFoundException("User or film not found");
        }

        log.debug("User with ID {} adds like to film with ID {}", userId, filmId);
        filmStorage.addLike(userId, filmId);
    }

    /**
     * Removes a like from a user to a film.
     *
     * @param filmId
     * @param userId
     * @throws NotFoundException
     */
    public void removeLike(long filmId, long userId) {
        Optional<Film> film = filmStorage.getById(filmId);
        Optional<User> user = userStorage.getById(userId);

        if (film.isEmpty() || user.isEmpty()) {
            throw new NotFoundException("User or film not found");
        }

        if (!filmStorage.getLikes(filmId).contains(userId)) {
            throw new NotFoundException("User didn't like this film before!");
        }

        log.debug("User with ID {} removes like from film with ID {}", userId, filmId);
        filmStorage.removeLike(userId, filmId);
    }

    /**
     * Generates a new unique ID for a film.
     *
     * @return {@link Long}
     */
    private Long generateId() {
        Long currentId = filmStorage.getAll().stream()
                .mapToLong(Film::getId)
                .max()
                .orElse(0);
        return ++currentId;
    }

}
